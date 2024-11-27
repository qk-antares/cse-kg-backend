package com.antares.kg.service;

import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import com.antares.kg.model.entity.CrawlTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Antares
* @description 针对表【crawl_task(爬取任务)】的数据库操作Service
* @createDate 2024-11-23 16:37:28
*/
public interface CrawlTaskService extends IService<CrawlTask> {
    void addCrawlTask(CrawlTaskAddReq crawlTaskAddReq);

    void stopCrawlTask(Long taskId);

    void restartCrawlTask(Long taskId);
}
