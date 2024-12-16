package com.antares.kg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天对话表
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat implements Serializable {
    /**
     * 对话id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户输入的提问
     */
    private String msg;

    /**
     * 提问类型（local，global）
     */
    private String type;

    /**
     * 对话状态，0是已发送，1是正在处理，2是处理成功，3是处理失败
     */
    private Integer status;

    /**
     * GraphRAG的回复
     */
    private String res;

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