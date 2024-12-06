package com.antares.kg.model.enums;

import lombok.Getter;

@Getter
public enum CrawlTypeEnum {
    // 维基百科
    WIKIPEDIA("wikipedia"),
    // 百度百科
    BAIDU_BAIKE("baike");

    public final String type;

    CrawlTypeEnum(String type) {
        this.type = type;
    }
}
