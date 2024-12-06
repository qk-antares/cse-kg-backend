package com.antares.kg.service;

import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;

@SpringBootTest
public class CrawlTaskServiceTest {
    @Resource
    private CrawlTaskService crawlTaskService;

    @Test
    public void testCrawlTaskService() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("数据清洗");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E6%B8%85%E6%B4%97/4402497");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);

        // 阻塞等待爬取的线程执行
        try {
            Thread.sleep(Duration.ofHours(24));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRestartCrawlTask() {
        crawlTaskService.restartCrawlTask(13L);
    }
}
