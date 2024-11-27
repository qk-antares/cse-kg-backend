package com.antares.kg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 百度百科词条
 * @TableName lemma
 */
@TableName(value ="lemma")
@Data
public class Lemma implements Serializable {
    /**
     * 词条id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 词条所处任务的深度
     */
    private Integer depth;

    /**
     * 词条url
     */
    private String url;

    /**
     * 词条名称
     */
    private String name;

    /**
     * 词条标题
     */
    private String title;

    /**
     * 词条和主题的相关性分数
     */
    private Integer score;

    /**
     * 词条内容txt保存路径
     */
    private String content;

    /**
     * 词条状态，0是等待爬取，1是爬取成功，2是爬取失败，3是爬取退出（相关性未达到阈值）
     */
    private Integer status;

    /**
     * 爬取日志，用于记录爬取失败时的信息
     */
    private String log;

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