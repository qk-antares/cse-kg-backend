package com.antares.kg.strategy.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import com.antares.kg.constant.CrawlerConstants;
import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.model.entity.Lemma;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.model.enums.LemmaStatusEnum;
import com.antares.kg.strategy.CrawlStrategy;
import com.antares.kg.utils.LemmaScoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class BaikeCrawlStrategy implements CrawlStrategy {
    /**
     * 爬取url下的百度百科词条
     *
     * @param url            词条链接
     * @param scoreThreshold 关联性分数阈值
     * @return 爬取结果
     */
    @Override
    public CrawlRes crawl(String url, int scoreThreshold) {
        Document doc;
        Lemma mainLemma = new Lemma();

        // 1. 检查url是否合法
        if(!ReUtil.isMatch(CrawlerConstants.BAIKE_URL_PATTERN, url)) {
            log.error("页面 {} 不是一个百度百科词条", url);
            mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
            mainLemma.setLog("页面不是一个百度百科词条\n");
            return CrawlRes.builder().mainLemma(mainLemma).build();
        }

        // 2. 获取页面
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("获取页面 {} 失败", url);
            mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
            mainLemma.setLog("获取页面失败:\n" + e.getMessage());
            return CrawlRes.builder().mainLemma(mainLemma).build();
        }

        try {
            List<Lemma> referenceLemmas = new ArrayList<>();
            HashSet<Integer> referenceIds = new HashSet<>();

            // 3.1 获取主体内容DOM
            Element mainContent = doc.selectFirst(CrawlerConstants.BAIKE_MAIN_CONTENT_SELECTOR);

            // 3.2 获取总结
            StringBuilder content = new StringBuilder();
            Element summary = Objects.requireNonNull(mainContent).selectFirst(CrawlerConstants.BAIKE_SUMMARY_SELECTOR);
            Elements summaryParas = Objects.requireNonNull(summary).select(CrawlerConstants.BAIKE_PARA_SELECTOR);
            for (Element summaryPara : summaryParas) {
                content.append(extractTextWithLinks(summaryPara, referenceLemmas, referenceIds)).append("\n");
            }

            // 3.3 根据总结对该词条评分
            int score = LemmaScoreUtil.scoreLemmaBySummary(content.toString());
            mainLemma.setScore(score);
            String rawTitle = doc.title().replace('/', '-');
            mainLemma.setTitle(rawTitle.substring(0, rawTitle.length() - CrawlerConstants.BAIKE_TITLE_SUFFIX_LENGTH));

            // 3.4 词条和主题的关联性太低 或者 分数获取失败
            if(score == -1) {
                mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
                mainLemma.setLog(String.format("获取分数失败：%s", content));
                return CrawlRes.builder().mainLemma(mainLemma).build();
            } else if (score < scoreThreshold) {
                mainLemma.setStatus(LemmaStatusEnum.EXIT.getCode());
                return CrawlRes.builder().mainLemma(mainLemma).build();
            }

            // 3.5 获取详细描述
            Element detail = mainContent.selectFirst(CrawlerConstants.BAIKE_DETAIL_SELECTOR);
            Elements detailSections = Objects.requireNonNull(detail).select(String.format("%s, %s", CrawlerConstants.BAIKE_TITLE_SELECTOR, CrawlerConstants.BAIKE_PARA_SELECTOR));
            for (Element section : detailSections) {
                if (section.hasClass(CrawlerConstants.BAIKE_TITLE_CLASS)) {
                    // 3.5.1 这是标题部分
                    Element title = section.selectFirst("h1, h2, h3, h4, h5, h6");
                    String tagName = Objects.requireNonNull(title).tagName();
                    int titleLevel = Integer.parseInt(tagName.substring(1));

                    content.append("\n").append("#".repeat(titleLevel)).append(" ").append(title.text()).append("\n");
                } else if (section.hasClass(CrawlerConstants.BAIKE_PARA_CLASS)) {
                    // 3.5.2 这是段落部分
                    content.append(extractTextWithLinks(section, referenceLemmas, referenceIds)).append("\n");
                }
            }

            mainLemma.setContent(content.toString());
            mainLemma.setStatus(LemmaStatusEnum.SUCCESS.getCode());
            return CrawlRes.builder().mainLemma(mainLemma).referenceLemmas(referenceLemmas).build();
        } catch (NullPointerException e) {
            log.error("{} 中获取DOM元素失败", url);
            mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
            mainLemma.setLog("获取DOM元素失败:\n" + e.getMessage());
            return CrawlRes.builder().mainLemma(mainLemma).build();
        }
    }

    /**
     * 提取 HTML 中的文本和引用词条
     *
     * @param element        HTML 元素
     * @param referenceLemmas 引用词条列表
     * @param referenceIds 引用链接（防止重复添加）
     * @return 提取后的纯文本
     */
    private static StringBuilder extractTextWithLinks(Element element, List<Lemma> referenceLemmas, Set<Integer> referenceIds) {
        StringBuilder text = new StringBuilder();

        // 遍历所有 <span> 标签
        for (Element span : element.select("span")) {
            // 1. 检查 <span> 内是否包含 <sup> （引用角标不需要加入到爬取的文本中）
            Element sup = span.selectFirst("sup");
            if(sup != null) {
                continue;
            }

            // 2. 检查 <span> 内是否包含 <a> （引用链接需要加入referenceLemmas）
            Element link = span.selectFirst("a");
            if (link != null) {
                // 提取文本和 URL
                text.append(link.text());
                String href = link.attr("href");
                if (href.startsWith("/item/")) {
                    // 把链接的后缀去掉
                    href = href.substring(0, href.length() - CrawlerConstants.BAIKE_LINK_SUFFIX_LENGTH);
                    String name = URLUtil.decode(ReUtil.get(CrawlerConstants.BAIKE_LINK_PATTERN, href, 1));
                    int id = Integer.parseInt(ReUtil.get(CrawlerConstants.BAIKE_LINK_PATTERN, href, 2));

                    // 这是一个有效的引用词条链接
                    if (!referenceIds.contains(id) && id != 0) {
                        Lemma pendingLemma = new Lemma();
                        pendingLemma.setName(name);
                        pendingLemma.setUrl(CrawlerConstants.BAIKE_LINK_PREFIX + href);
                        pendingLemma.setStatus(LemmaStatusEnum.PENDING.getCode());

                        referenceLemmas.add(pendingLemma);
                        referenceIds.add(id);
                    }
                }
            } else {
                // 提取纯文本
                text.append(span.text());
            }
        }

        return text;
    }
}
