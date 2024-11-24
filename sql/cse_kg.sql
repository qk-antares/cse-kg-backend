create database cse_kg;

create table `crawl_task` (
    `id` bigint not null auto_increment comment '爬取任务id',
    `root_url` varchar(512) not null comment '根词条url',
    `root_name` varchar(128) not null comment '根词条名称',
    `type` varchar(32) not null default 'baike' comment '爬取类型（baike，wikipedia）',
    `score_threshold` int not null default 6 comment '相关性阈值',
    `max_depth` int default 1 comment '爬取的最大深度',
    `cur_depth` int default 0 comment '当前爬取完成的深度',
    `count` int default 0 comment '已爬取词条数量',
    `status` tinyint not null default 0 comment '任务状态，0是等待执行，1是正在执行，2是执行完成，3是执行失败',
    `log` text default null comment '任务日志，用于记录任务执行失败时的信息',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爬取任务';

create table `lemma` (
    `id` bigint not null auto_increment comment '词条id',
    `task_id` bigint not null comment '任务id',
    `depth` int not null comment '词条所处任务的深度',
    `url` varchar(512) not null comment '词条url',
    `name` varchar(128) not null comment '词条名称',
    `title` varchar(128) default null comment '词条标题',
    `content` varchar(256) default null comment '词条内容txt保存路径',
    `status` tinyint not null default 0 comment '词条状态，0是等待爬取，1是爬取成功，2是爬取失败，3是爬取退出（相关性未达到阈值）',
    `log` text default null comment '爬取日志，用于记录爬取失败时的信息',
    `score` int default 0 comment '词条和主题的相关性分数',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_taskId_status_depth_createTime` (`task_id`, `status`, `depth`, `create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='百度百科词条';

create table `lemma_link` (
    `id` bigint not null auto_increment comment '关系id',
    `from_lemma_id` bigint not null comment '父词条id',
    `to_lemma_id` bigint not null comment '被引用词条id',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='词条关系表';

# 下一个待爬取词条
EXPLAIN SELECT *
FROM `lemma`
WHERE `task_id` = :task_id
  AND `status` = 0
ORDER BY `depth` ASC, `create_time` ASC
LIMIT 1;