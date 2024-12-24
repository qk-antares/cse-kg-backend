package com.antares.kg.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 实体表
 * @TableName entity
 */
@TableName(value ="entity")
@Data
public class Entity implements Serializable {
    /**
     * 实体id，对应human_readable_id
     */
    @TableId
    private Long id;

    /**
     * 实体标题
     */
    private String title;

    /**
     * 实体类型
     */
    private String type;

    /**
     * 实体描述
     */
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}