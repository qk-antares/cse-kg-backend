package com.antares.kg.model.dto.chat;

import lombok.Data;

/**
 * 关系表
 * @TableName relationship
 */
@Data
public class Relationship {
    private Integer id;
    private String source;
    private String target;
    private String description;
    private Double weight;
}