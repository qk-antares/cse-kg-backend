package com.antares.kg.model.enums;

import lombok.Getter;

@Getter
public enum CrawlTypeEnum {
    // 维基百科
    WIKIPEDIA("wikipedia"),
    //请求的资源不存在
    BAIDU_BAIKE("baike");

    public final String type;

    CrawlTypeEnum(String type) {
        this.type = type;
    }
}
