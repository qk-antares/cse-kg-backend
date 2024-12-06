package com.antares.kg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.antares.kg.exception.BusinessException;
import com.antares.kg.mapper.LemmaMapper;
import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import com.antares.kg.model.entity.CrawlTask;
import com.antares.kg.model.entity.Lemma;
import com.antares.kg.model.enums.CrawlTaskStatusEnum;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.service.LemmaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.kg.service.CrawlTaskService;
import com.antares.kg.mapper.CrawlTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Antares
 * @description 针对表【crawl_task(爬取任务)】的数据库操作Service实现
 * @createDate 2024-11-23 16:37:28
 */
@Service
@Slf4j
public class CrawlTaskServiceImpl extends ServiceImpl<CrawlTaskMapper, CrawlTask>
        implements CrawlTaskService {
    @Resource
    private LemmaMapper lemmaMapper;
    @Resource
    private LemmaService lemmaService;

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private static final ConcurrentHashMap<Long, Future<?>> taskMap = new ConcurrentHashMap<>();

    @Override
    public void addCrawlTask(CrawlTaskAddReq crawlTaskAddReq) {
        CrawlTask crawlTask = BeanUtil.copyProperties(crawlTaskAddReq, CrawlTask.class);
        crawlTask.setStatus(CrawlTaskStatusEnum.PENDING.code);
        crawlTask.setLog("任务已创建并提交...\n");
        this.save(crawlTask);

        // 添加根词条
        Lemma rootLemma = new Lemma();
        rootLemma.setTaskId(crawlTask.getId());
        rootLemma.setDepth(1);
        rootLemma.setName(crawlTask.getRootName());
        rootLemma.setUrl(crawlTask.getRootUrl());
        lemmaMapper.insert(rootLemma);

        Future<?> future = threadPool.submit(() -> startCrawlTask(crawlTask));
        taskMap.put(crawlTask.getId(), future);
    }

    @Override
    public void restartCrawlTask(Long taskId) {
        CrawlTask crawlTask = getById(taskId);
        if (crawlTask == null) {
            throw new BusinessException(HttpCodeEnum.NOT_EXIST, "任务不存在");
        }

        Future<?> future = threadPool.submit(() -> startCrawlTask(crawlTask));
        taskMap.put(crawlTask.getId(), future);

        crawlTask.setStatus(CrawlTaskStatusEnum.EXECUTING.code);
        crawlTask.setLog(crawlTask.getLog() + "任务重新启动...\n");
        this.updateById(crawlTask);
    }

    @Override
    public void stopCrawlTask(Long taskId) {
        Future<?> future = taskMap.remove(taskId);
        if (future == null) {
            throw new BusinessException(HttpCodeEnum.NOT_EXIST, "任务不存在");
        }

        CrawlTask crawlTask = getById(taskId);
        future.cancel(true);

        crawlTask.setStatus(CrawlTaskStatusEnum.STOPED.code);
        crawlTask.setLog(crawlTask.getLog() + "任务被手动暂停...\n");
        this.updateById(crawlTask);
    }

    public void startCrawlTask(CrawlTask crawlTask) {
        crawlTask.setStatus(CrawlTaskStatusEnum.EXECUTING.code);
        crawlTask.setLog(crawlTask.getLog() + "任务开始执行...\n");
        this.updateById(crawlTask);

        Long taskId = crawlTask.getId();
        Lemma nextLemma;
        do {
            // 查询下一个待爬词条
            nextLemma = lemmaMapper.getNextLemma(taskId);
            if (nextLemma != null) {
                lemmaService.crawlOneLemma(crawlTask, nextLemma);
            }
        } while (nextLemma != null);

        taskMap.remove(taskId);

        crawlTask.setStatus(CrawlTaskStatusEnum.SUCCUESS.code);
        crawlTask.setLog(crawlTask.getLog() + "任务执行成功！\n");
        this.updateById(crawlTask);
    }
}

