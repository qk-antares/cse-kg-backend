package com.antares.kg.controller;

import com.antares.kg.model.dto.crawl.CrawlTaskAddReq;
import com.antares.kg.service.CrawlTaskService;
import com.antares.kg.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/crawl")
public class CrawlController {
    @Resource
    private CrawlTaskService crawlTaskService;

    @PostMapping
    public R<Void> addCrawlTask(@RequestBody CrawlTaskAddReq crawlTaskAddReq) {
        crawlTaskService.addCrawlTask(crawlTaskAddReq);
        return R.ok();
    }
}