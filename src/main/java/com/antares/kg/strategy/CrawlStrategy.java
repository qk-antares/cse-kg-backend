package com.antares.kg.strategy;

import com.antares.kg.model.dto.crawl.CrawlRes;

import java.io.IOException;

public interface CrawlStrategy {
    CrawlRes crawl(String url, int scoreThreshold);
}
