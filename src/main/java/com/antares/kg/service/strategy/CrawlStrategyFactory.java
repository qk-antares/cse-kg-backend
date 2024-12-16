package com.antares.kg.service.strategy;

import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.service.strategy.impl.BaikeCrawlStrategy;
import com.antares.kg.service.strategy.impl.WikipediaCrawlStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CrawlStrategyFactory {
    @Resource
    private BaikeCrawlStrategy baikeCrawlStrategy;
    @Resource
    private WikipediaCrawlStrategy wikipediaCrawlStrategy;

    /**
     * 创建爬虫策略
     *
     * @param type 爬虫类型
     * @return
     */
    public CrawlStrategy getStrategy(String type) {
        return switch (type) {
            case "baike" -> baikeCrawlStrategy;
            case "wikipedia" -> wikipediaCrawlStrategy;
            default -> throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "不支持的爬虫类型：" + type);
        };
    }
}
