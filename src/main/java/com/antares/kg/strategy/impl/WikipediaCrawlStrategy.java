package com.antares.kg.strategy.impl;

import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.strategy.CrawlStrategy;
import org.springframework.stereotype.Component;

@Component
public class WikipediaCrawlStrategy implements CrawlStrategy {
    // Todo: 爬取维基百科词条
    @Override
    public CrawlRes crawl(String url, int scoreThreshold) {
        return null;
    }
}
