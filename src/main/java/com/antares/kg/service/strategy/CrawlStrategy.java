package com.antares.kg.service.strategy;

import com.antares.kg.model.dto.crawl.CrawlRes;

public interface CrawlStrategy {
    /**
     * 根据链接和分数阈值爬取词条
     * 当词条的相关性未达到阈值时，返回的CrawlRes中只包含最新的mainLemma（title、score和status（爬取退出）），否则：
     * 返回最新的mianLemma（title、score、content和status（爬取成功））和其（有效的）引用的lemmas
     * 对于引用列表中的lemma，你只用设置其name、url和status（待爬取）属性即可
     * @param url 词条链接
     * @param scoreThreshold 分数阈值
     * @return 爬取结果
     */
    CrawlRes crawl(String url, int scoreThreshold);
}
