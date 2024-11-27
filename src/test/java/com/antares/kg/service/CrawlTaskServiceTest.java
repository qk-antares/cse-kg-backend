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
    public void testCrawlTaskService1() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("计算机科学");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%e8%ae%a1%e7%ae%97%e6%9c%ba%e7%a7%91%e5%ad%a6/9132");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }

    @Test
    public void testCrawlTaskService2() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("机器学习");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%E6%9C%BA%E5%99%A8%E5%AD%A6%E4%B9%A0/217599");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }

    @Test
    public void testCrawlTaskService3() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("软件设计模式");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%E8%BD%AF%E4%BB%B6%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F/2117635");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }

    @Test
    public void testCrawlTaskService4() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("图神经网络");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%E5%9B%BE%E7%A5%9E%E7%BB%8F%E7%BD%91%E7%BB%9C/59091829");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }

    @Test
    public void testCrawlTaskService5() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("软件工程");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/%E8%BD%AF%E4%BB%B6%E5%B7%A5%E7%A8%8B/25279");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }

    @Test
    public void testCrawlTaskService6() {
        CrawlTaskAddReq crawlTaskAddReq = new CrawlTaskAddReq();
        crawlTaskAddReq.setRootName("Linux");
        crawlTaskAddReq.setType("baike");
        crawlTaskAddReq.setRootUrl("https://baike.baidu.com/item/Linux/27050");
        crawlTaskAddReq.setMaxDepth(2);
        crawlTaskAddReq.setScoreThreshold(6);

        crawlTaskService.addCrawlTask(crawlTaskAddReq);
    }

    @Test
    public void testRestartCrawlTask() {
        crawlTaskService.restartCrawlTask(8L);
    }
}
