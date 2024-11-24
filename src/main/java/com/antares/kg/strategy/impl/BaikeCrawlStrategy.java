package com.antares.kg.strategy.impl;

import com.antares.kg.constant.CrawlerConstants;
import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.strategy.CrawlStrategy;
import com.antares.kg.utils.LemmaScoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BaikeCrawlStrategy implements CrawlStrategy {
    /**
     * 爬取url下的百度百科词条，当该词条和我们的主题（计算机科学）关联性太低时，不进行爬取，返回一个空的CrawlRes
     *
     * @param url            词条链接
     * @param scoreThreshold 关联性分数阈值
     * @return 爬取结果
     */
    @Override
    public CrawlRes crawl(String url, int scoreThreshold) {
        List<String> referenceLinks = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, "获取页面失败，请检查url参数是否正确！");
        }

        // 获取主体内容DOM
        Element mainContent = doc.selectFirst(CrawlerConstants.BAIKE_MAIN_CONTENT_SELECTOR);
        if (mainContent == null) {
            throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, String.format("未在页面 [%s] 获取主体内容DOM，请检查url参数是否正确", url));
        }

        // 获取总结
        StringBuilder content = new StringBuilder();
        Element summary = mainContent.selectFirst(CrawlerConstants.BAIKE_SUMMARY_SELECTOR);
        Elements summaryParas = Objects.requireNonNull(summary).select(CrawlerConstants.BAIKE_PARA_SELECTOR);
        for (Element summaryPara : summaryParas) {
            content.append(extractTextWithLinks(summaryPara, referenceLinks)).append("\n");
        }

        // 根据总结对该词条评分
        int score = LemmaScoreUtil.scoreLemmaBySummary(content.toString());
        // 词条和主题的关联性太低
        if (score < scoreThreshold) {
            return CrawlRes.builder().title(doc.title()).score(score).build();
        }

        // 获取详细描述
        Element detail = mainContent.selectFirst(CrawlerConstants.BAIKE_DETAIL_SELECTOR);
        Elements detailSections = Objects.requireNonNull(detail).select(String.format("%s, %s", CrawlerConstants.BAIKE_TITLE_SELECTOR, CrawlerConstants.BAIKE_PARA_SELECTOR));

        for (Element section : detailSections) {
            if (section.hasClass(CrawlerConstants.BAIKE_TITLE_CLASS)) {
                // 这是标题部分
                Element title = section.selectFirst("h1, h2, h3, h4, h5, h6");
                String tagName = Objects.requireNonNull(title).tagName();
                int titleLevel = Integer.parseInt(tagName.substring(1));

                content.append("\n").append("#".repeat(titleLevel)).append(" ").append(title.text()).append("\n");
            } else if (section.hasClass(CrawlerConstants.BAIKE_PARA_CLASS)) {
                // 这是段落部分
                content.append(extractTextWithLinks(section, referenceLinks)).append("\n");
            }
        }

        return CrawlRes.builder().title(doc.title()).content(content.toString()).score(score).referenceLinks(referenceLinks).build();
    }


    /**
     * 提取 HTML 中的文本和链接
     *
     * @param element        HTML 元素
     * @param referenceLinks 存储百度百科链接的列表
     * @return 提取后的纯文本
     */
    private static StringBuilder extractTextWithLinks(Element element, List<String> referenceLinks) {
        StringBuilder text = new StringBuilder();

        // 遍历所有 <span> 标签
        for (Element span : element.select("span")) {
            // 检查 <span> 内是否包含 <a>
            Element link = span.selectFirst("a");
            if (link != null) {
                // 提取文本和 URL
                text.append(link.text());
                String href = link.attr("href");
                if (href.startsWith("/item/")) {
                    referenceLinks.add(href.substring(0, href.length() - CrawlerConstants.BAIKE_LINK_SUFFIX_LENGTH));
                }
            } else {
                // 提取纯文本
                text.append(span.text());
            }
        }

        return text;
    }
}
