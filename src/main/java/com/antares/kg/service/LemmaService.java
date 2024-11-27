package com.antares.kg.service;

import com.antares.kg.model.entity.CrawlTask;
import com.antares.kg.model.entity.Lemma;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Antares
* @description 针对表【lemma(百度百科词条)】的数据库操作Service
* @createDate 2024-11-23 16:37:28
*/
public interface LemmaService extends IService<Lemma> {
    /**
     * 执行crawlTask中的待爬取lemma
     * @param crawlTask
     * @param lemma 注意该lemma的状态必须是pending
     * @return
     */
    boolean crawlOneLemma(CrawlTask crawlTask, Lemma lemma);
}
