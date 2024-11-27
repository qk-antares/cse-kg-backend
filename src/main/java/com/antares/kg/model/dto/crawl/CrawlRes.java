package com.antares.kg.model.dto.crawl;

import com.antares.kg.model.entity.Lemma;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CrawlRes {
    private Lemma mainLemma;
    private List<Lemma> referenceLemmas;
}
