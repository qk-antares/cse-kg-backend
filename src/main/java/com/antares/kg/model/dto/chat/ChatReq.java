package com.antares.kg.model.dto.chat;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ChatReq {
    @NotBlank(message = "消息不能为空")
    private String msg;
    @Pattern(regexp = "local|global", message = "不支持的查询类型")
    private String type;
}
