package com.antares.kg.model.enums;

public enum CrawlTaskStatusEnum {
    PENDING(0, "等待执行"),
    EXECUTING(1, "正在执行"),
    SUCCUESS(2, "执行成功"),
    STOPED(3, "执行停止");

    public final int code;
    public final String description;

    CrawlTaskStatusEnum(int code, String description){
        this.code = code;
        this.description = description;
    }
}
