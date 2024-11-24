package com.antares.kg.service;

import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class CrawlTaskServiceTest {
    @Resource
    private CrawlTaskService crawlTaskService;

    @Test
    public void testCrawlTaskService() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("计算机科学");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%e8%ae%a1%e7%ae%97%e6%9c%ba%e7%a7%91%e5%ad%a6/9132");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }
}
