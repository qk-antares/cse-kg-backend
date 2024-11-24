package com.antares.kg;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class BaiduBaikeCrawler {
    /**
     * 爬取百度百科词条
     * @param url 词条的完整 URL
     * @return 包含的百度百科链接列表
     * @throws IOException 如果爬取或解析过程中出错
     */
    public static List<String> crawlBaiduBaike(String url) throws IOException {
        // 存储百度百科词条链接
        List<String> baikeLinks = new ArrayList<>();

        // 爬取页面
        Document doc = Jsoup.connect(url).get();

        // 提取标题
        String title = doc.title();
        log.info("Crawling: {}", title);

        // 提取主体内容
        Element contentTab = doc.selectFirst("div[class^=contentTab_]");

        if (contentTab == null) {
            log.error("ContentTab not found for: {}", url);
            return baikeLinks;
        }

        // 提取详细描述
        StringBuilder detailBuilder = new StringBuilder();
        Elements detailSections = contentTab.select("div.para_yeVDI, div.paraTitle_MDWuj");
        for (Element section : detailSections) {
            if (section.hasClass("paraTitle_MDWuj")) {
                // 这是标题部分
                detailBuilder.append("\n## ").append(Objects.requireNonNull(section.selectFirst("h1, h2, h3, h4, h5, h6")).text()).append("\n");
            } else if (section.hasClass("para_yeVDI")) {
                // 这是段落部分
                detailBuilder.append(extractTextWithLinks(section, baikeLinks)).append("\n");
            }
        }

        // 保存到文件
        String fileName = title.replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(detailBuilder + "\n");
            System.out.println("Saved content to file: " + fileName);
        }

        return baikeLinks;
    }

    /**
     * 提取 HTML 元素中的文本，包括处理 <a> 标签中的超链接
     *
     * @param element    HTML 元素
     * @param baikeLinks 存储百度百科链接的列表
     * @return 提取后的纯文本
     */
    private static String extractTextWithLinks(Element element, List<String> baikeLinks) {
        StringBuilder text = new StringBuilder();

        // 遍历子元素
        for (Element span : element.select("span")) { // 针对所有 <span> 标签
            Element link = span.selectFirst("a"); // 检查 <span> 内是否包含 <a>
            if (link != null) {
                // 提取链接文本和 URL
                text.append(link.text());
                String href = link.attr("href");
                if (href.startsWith("/item/")) {
                    baikeLinks.add("https://baike.baidu.com" + href);
                }
            } else {
                // 提取纯文本
                text.append(span.text());
            }
        }

        return text.toString();
    }

    public static void main(String[] args) {
        try {
            String startUrl = "https://baike.baidu.com/item/计算机科学/9132";
            List<String> links = crawlBaiduBaike(startUrl);
            System.out.println("Extracted links:");
            for (String link : links) {
                System.out.println(link);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
