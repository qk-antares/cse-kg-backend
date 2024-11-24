package com.antares.kg.model.dto.crawl;

import lombok.Data;

@Data
public class CrawlTaskAddReq {
    String rootUrl;
    String rootName;
    String type;
    int scoreThreshold;
    int maxDepth;
}
