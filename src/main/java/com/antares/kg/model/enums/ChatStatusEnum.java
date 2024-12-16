package com.antares.kg.model.enums;

public enum ChatStatusEnum {
    SUBMITTED(0, "已发送"),
    PROCESSING(1, "正在处理"),
    SUCCUESS(2, "处理成功"),
    FAILED(3, "处理失败");

    public final int code;
    public final String description;

    ChatStatusEnum(int code, String description){
        this.code = code;
        this.description = description;
    }
}
