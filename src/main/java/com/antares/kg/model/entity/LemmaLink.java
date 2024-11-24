package com.antares.kg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 词条关系表
 * @TableName lemma_link
 */
@TableName(value ="lemma_link")
@Data
public class LemmaLink implements Serializable {
    /**
     * 关系id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父词条id
     */
    private Long fromLemmaId;

    /**
     * 被引用词条id
     */
    private Long toLemmaId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}