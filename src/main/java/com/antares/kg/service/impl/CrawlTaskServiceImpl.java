package com.antares.kg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import com.antares.kg.constant.CrawlerConstants;
import com.antares.kg.mapper.LemmaMapper;
import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import com.antares.kg.model.entity.CrawlTask;
import com.antares.kg.model.entity.Lemma;
import com.antares.kg.model.enums.LemmaStatusEnum;
import com.antares.kg.strategy.CrawlStrategy;
import com.antares.kg.strategy.CrawlStrategyFactory;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.kg.service.CrawlTaskService;
import com.antares.kg.mapper.CrawlTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
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
    private CrawlStrategyFactory crawlStrategyFactory;

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


    private void startCrawlTask(CrawlTask crawlTask) {
        Long taskId = crawlTask.getId();
        Lemma lemma;
        do {
            // 1. 查询下一个待爬词条
            lemma = lemmaMapper.getNextLemma(taskId);
            if (lemma != null) {
                // 2. 使用特定的策略爬取词条
                CrawlStrategy strategy = crawlStrategyFactory.getStrategy(crawlTask.getType());
                CrawlRes crawlRes = strategy.crawl(lemma.getUrl(), crawlTask.getScoreThreshold());

                // 3. 更新该词条的爬取状态
                lemma.setStatus(LemmaStatusEnum.SUCCESS.getCode());
                lemma.setTitle(crawlRes.getTitle());
                lemma.setScore(crawlRes.getScore());


                String path = String.format("baike/%d-%d-%s.txt", taskId, lemma.getId(), lemma.getTitle());
                lemma.setContent(path);
                FileUtil.writeUtf8String(crawlRes.getContent(), path);
                log.info("词条保存路径: {}", path);
                lemmaMapper.updateById(lemma);

                Integer depth = lemma.getDepth();
                // 4. 如果未达到指定的爬取深度，还需要添加待爬词条
                if(depth < crawlTask.getMaxDepth()) {
                    List<Lemma> pendingLemmas = crawlRes.getReferenceLinks().stream().map(link -> {
                        String pattern = "/item/(.*?)/(\\d+)";
                        String name = ReUtil.get(pattern, link, 0); // 提取第一个分组
                        int id = Integer.parseInt(ReUtil.get(pattern, link, 1));   // 提取第二个分组
                        if (id != 0) {
                            Lemma pendingLemma = new Lemma();
                            pendingLemma.setTaskId(taskId);
                            pendingLemma.setName(name);
                            pendingLemma.setDepth(depth + 1);
                            pendingLemma.setUrl(CrawlerConstants.BAIKE_LINK_PREFIX + link);
                            return pendingLemma;
                        } else {
                            return null;
                        }
                    }).filter(Objects::nonNull).toList();
                    lemmaMapper.insert(pendingLemmas);
                }
            }
        } while (lemma != null);

        taskMap.remove(taskId);
    }
}




