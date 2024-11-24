package com.antares.kg.model.dto.crawl;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CrawlRes {
    private String title;
    private String content;
    private Integer score;

    private List<String> referenceLinks;
}
