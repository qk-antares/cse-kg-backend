package com.antares.kg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.antares.kg.mapper.LemmaLinkMapper;
import com.antares.kg.mapper.LemmaMapper;
import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import com.antares.kg.model.entity.CrawlTask;
import com.antares.kg.model.entity.Lemma;
import com.antares.kg.model.entity.LemmaLink;
import com.antares.kg.model.enums.LemmaStatusEnum;
import com.antares.kg.service.LemmaService;
import com.antares.kg.strategy.CrawlStrategy;
import com.antares.kg.strategy.CrawlStrategyFactory;
import com.antares.kg.utils.MinioUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.kg.service.CrawlTaskService;
import com.antares.kg.mapper.CrawlTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
        this.save(crawlTask);

        Lemma rootLemma = new Lemma();
        rootLemma.setTaskId(crawlTask.getId());
        rootLemma.setDepth(1);
        rootLemma.setName(crawlTask.getRootName());
        rootLemma.setUrl(crawlTask.getRootUrl());
        lemmaMapper.insert(rootLemma);

        Future<?> future = threadPool.submit(() -> startCrawlTask(crawlTask));
        taskMap.put(crawlTask.getId(), future);

        try {
            Thread.sleep(6000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restartCrawlTask(Long taskId) {
        CrawlTask crawlTask = getById(taskId);
        crawlTask.setLog(crawlTask.getLog() + "任务重新启动...\n");
        Future<?> future = threadPool.submit(() -> startCrawlTask(crawlTask));
        taskMap.put(crawlTask.getId(), future);

        try {
            Thread.sleep(6000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopCrawlTask(Long taskId) {
        Future<?> future = taskMap.remove(taskId);
        if (future != null) {
            CrawlTask crawlTask = getById(taskId);
            crawlTask.setLog(crawlTask.getLog() + "任务被手动暂停...\n");
            future.cancel(true);
        }
    }

    public void startCrawlTask(CrawlTask crawlTask) {
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
    }
}

