package com.antares.kg.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 关系表
 * @TableName relationship
 */
@TableName(value ="relationship")
@Data
public class Relationship implements Serializable {
    /**
     * 关系id，对应human_readable_id
     */
    @TableId
    private Long id;

    /**
     * 源实体标题
     */
    private String source;

    /**
     * 目标实体标题
     */
    private String target;

    /**
     * 关系描述
     */
    private String description;

    /**
     * 关系的重要性权重
     */
    private Double weight;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}