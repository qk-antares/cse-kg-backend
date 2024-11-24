package com.antares.kg.model.enums;

import lombok.Getter;

@Getter
public enum LemmaStatusEnum {
    PENDING(0,"等待爬取"),
    SUCCESS(1, "爬取成功"),
    ERROR(2, "爬取失败");

    public final int code;
    public final String description;

    LemmaStatusEnum(int code, String description){
        this.code = code;
        this.description = description;
    }
}
