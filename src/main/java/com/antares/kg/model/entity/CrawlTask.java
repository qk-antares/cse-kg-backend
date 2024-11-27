package com.antares.kg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 爬取任务
 * @TableName crawl_task
 */
@TableName(value ="crawl_task")
@Data
public class CrawlTask implements Serializable {
    /**
     * 爬取任务id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 根词条url
     */
    private String rootUrl;

    /**
     * 根词条名称
     */
    private String rootName;

    /**
     * 爬取类型（baike，wikipedia）
     */
    private String type;

    /**
     * 相关性阈值
     */
    private Integer scoreThreshold;

    /**
     * 爬取的最大深度
     */
    private Integer maxDepth;

    /**
     * 任务状态，0是等待执行，1是正在执行，2是执行完成，3是执行失败
     */
    private Integer status;

    /**
     * 任务日志，用于记录任务执行失败时的信息
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