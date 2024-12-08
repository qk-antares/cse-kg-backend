package com.antares.kg.strategy.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;

import com.antares.kg.constant.CrawlerConstants;

import com.antares.kg.model.dto.crawl.CrawlRes;
import com.antares.kg.model.entity.Lemma;

import com.antares.kg.model.enums.LemmaStatusEnum;
import com.antares.kg.strategy.CrawlStrategy;
import com.antares.kg.utils.LemmaScoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

@Slf4j
@Component
public class WikipediaCrawlStrategy implements CrawlStrategy {
    /**
     * 爬取url下的维基百科词条
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
        if (!ReUtil.isMatch(CrawlerConstants.WIKI_URL_PATTERN, url)) {
            log.error("页面 {} 不是一个维基百科词条", url);
            mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
            mainLemma.setLog("页面不是一个维基百科词条\n");
            return CrawlRes.builder().mainLemma(mainLemma).build();
        }

        // 2. 获取页面
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CrawlerConstants.PROXYHOST, CrawlerConstants.PROXYPORT));
            doc = Jsoup.connect(url).proxy(proxy).get();
        } catch (IOException e) {
            log.error("获取页面 {} 失败", url);
            mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
            mainLemma.setLog("获取页面失败:\n" + e.getMessage());
            return CrawlRes.builder().mainLemma(mainLemma).build();
        }


        // 3. 解析页面
        try {
            List<Lemma> referenceLemmas = new ArrayList<>();
            HashSet<Integer> referenceIds = new HashSet<>();

            // 3.1 获取主体内容DOM
            // 这里如果抛出异常，这个条目中可能只有标题，没有实际内容，例如 有效可计算性 这个词条
            Element mainContent = doc.selectFirst(CrawlerConstants.WIKI_MAIN_CONTENT_SELECTOR);


            // 3.2 获取总结
            StringBuilder content = new StringBuilder();
            Elements summarySections = Objects.requireNonNull(mainContent).select(String.format("%s, %s", CrawlerConstants.WIKI_TITLE_SELECTOR, CrawlerConstants.WIKI_PARA_SELECTOR));
            // 由于总结和详细介绍都在 mainContent 中，并且 mainContent 中第一个标题是详细内容的开始，所以需要判断总结内容在哪里结束
            for (Element section : summarySections) {
                // 如果遇到详细介绍的标题，则意味着总结部分结束
                if (section.hasClass(CrawlerConstants.WIKI_TITLE_CLASS)) {
                    break;
                } else if (section.tagName().equals("p")) {
                    content.append(extractTextWithLinks(section, referenceLemmas, referenceIds)).append("\n");
                }
            }


            // 3.3 根据总结对该词条评分
            int score = LemmaScoreUtil.scoreLemmaBySummary(ConvertToSimplified(content.toString()));
            mainLemma.setScore(score);
            String rawTitle = doc.title().replace('/', '-');  // Wiki百科中的标题中可能包含斜杠
            mainLemma.setTitle(ConvertToSimplified(rawTitle.substring(0, rawTitle.length() - CrawlerConstants.WIKI_TITLE_SUFFIX_LENGTH)) );


            // 3.4 词条和主题的关联性太低 或者 分数获取失败
            if (score == -1) {
                mainLemma.setStatus(LemmaStatusEnum.ERROR.getCode());
                mainLemma.setLog(String.format("获取分数失败：%s", content));
                return CrawlRes.builder().mainLemma(mainLemma).build();
            } else if (score < scoreThreshold) {
                mainLemma.setStatus(LemmaStatusEnum.EXIT.getCode());
                return CrawlRes.builder().mainLemma(mainLemma).build();
            }
            // 3.5 获取详细描述
            // 由于总结和详细介绍都在 mainContent 中，并且 mainContent 中第一个标题是详细内容的开始，所以需要过滤掉之前的总结内容
            boolean isDetail = false;
            boolean hasNotAppend = false;
            // 构建选择器字符串，由于维基百科的页面结构比较复杂，所以需要选择多个元素
            String selectors = String.format("%s, %s, %s, %s, %s, %s, %s",
                    CrawlerConstants.WIKI_TITLE_SELECTOR,
                    CrawlerConstants.WIKI_PARA_SELECTOR,
                    "dt",
                    "dd",
                    "ol",
                    "ul",
                    "tr");

            // 选择元素
            Elements detailSections = Objects.requireNonNull(mainContent).select(selectors);

            for (Element section : detailSections) {
                // 是标题，如果遇到第一个标题，则意味着接下来是详细内容
                if (section.hasClass(CrawlerConstants.WIKI_TITLE_CLASS)) {
                    isDetail = true;
                    hasNotAppend = false;
                    // 3.5.1 这是标题部分
                    Element title = section.selectFirst("h1, h2, h3, h4, h5, h6");
                    String tagName = Objects.requireNonNull(title).tagName();
                    int titleLevel = Integer.parseInt(tagName.substring(1));
                    // 将 title 转换成简体中文
                    String simplifiedTitle = ConvertToSimplified(title.text());

                    List<String> excludedTitles = Arrays.asList("参考文献", "参考资料","外部连接","外部链接","注解","脚注","延伸阅读","注释","外部连结","资料来源","引用","参考","参见");

                    // 如果到了相关条目，内容不是该词条的介绍，但是之后的内容一般是有<a>标签的，所以不添加内容，但是要处理<a>标签
                    List<String> notAppend = Arrays.asList("相关条目","关联项目","相关","相关内容");
                    if (notAppend.contains(simplifiedTitle)) {hasNotAppend = true;continue;}
                    // 如果到了 参考文献 标题，就停止
                    if (excludedTitles.contains(simplifiedTitle)){break;}

                    content.append("\n").append("#".repeat(titleLevel)).append(" ").append(simplifiedTitle).append("\n");
                } else if (isDetail) {
                    if (section.tagName().equals("p")) {
                        // 3.5.2 这是段落部分
                        StringBuilder stringBuilder = extractTextWithLinks(section, referenceLemmas, referenceIds);
                        if (!hasNotAppend){content.append(stringBuilder).append("\n");}
                    }
                    // 3.5.3 处理其他标签元素
                    else if (section.tagName().equals("dt")){
                        String s = ConvertToSimplified(section.text());
                        if (!hasNotAppend){content.append("**").append(s).append("**\n");}
                    } else if (section.tagName().equals("dd")) {
                        StringBuilder stringBuilder = extractTextWithLinks(section, referenceLemmas, referenceIds);
                        if (!hasNotAppend){ content.append(stringBuilder).append("\n");}
                    } else if (section.tagName().equals("ol")) {
                        Elements lis = section.select("li");
                        int no = 1;
                        for (Element li : lis) {
                            StringBuilder stringBuilder = extractTextWithLinks(li, referenceLemmas, referenceIds);
                            if (!hasNotAppend){content.append(no).append(". ").append(stringBuilder).append("\n");}
                            no += 1;
                        }
                    } else if (section.tagName().equals("ul")) {
                        Elements lis = section.select("li");
                        for (Element li : lis) {
                            StringBuilder stringBuilder = extractTextWithLinks(li, referenceLemmas, referenceIds);
                            if (!hasNotAppend){content.append("· ").append(stringBuilder).append("\n");}
                        }
                    } else if (section.tagName().equals("tr")) {
                        StringBuilder stringBuilder = extractTextWithLinks(section, referenceLemmas, referenceIds);
                        if (!hasNotAppend){content.append(stringBuilder).append("\n");}
                    }
                }
            }

            mainLemma.setContent(ConvertToSimplified(content.toString()));
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
     * @param element         HTML 元素
     * @param referenceLemmas 引用词条列表
     * @param referenceIds    引用链接（防止重复添加）
     * @return 提取后的纯文本
     */
    private static StringBuilder extractTextWithLinks(Element element, List<Lemma> referenceLemmas, Set<Integer> referenceIds) {

        // 遍历所有 <a> 标签
        for (Element link : element.select("a")) {

            String href = link.attr("href");
            if (ReUtil.isMatch(CrawlerConstants.WIKI_LINK_PATTERN, href)) {
                String name = URLUtil.decode(href.substring(CrawlerConstants.WIKI_PREFIX_LENGTH));

                /*
                 * 示例 URL:
                 * https://zh.wikipedia.org/wiki/计算机科学
                 * https://zh.wikipedia.org/wiki?curid=25
                 * 这两个 URL 代表同一个词条，可以通过词条名称 "计算机科学" 来获取词条的 ID。
                 * 维基百科中通常使用第一种格式。
                 * 另外，这些格式也是有效的：
                 * https://zh.wikipedia.org/wiki/计算机科学?curid=25
                 * https://zh.wikipedia.org/wiki?curid=25/计算机科学
                 */
                // 这里 id 表示词条的 curid 例如计算机科学的 curid 为 25
                // https://zh.wikipedia.org/wiki?curid=0 显示为标题无效
                int id = 0;
                id = getID(name);

                // 这是一个有效的引用词条链接
                if (!referenceIds.contains(id) && id != 0) {
                    Lemma pendingLemma = new Lemma();

                    pendingLemma.setName(ConvertToSimplified(name));

                    // 这里得到的 url 格式为 https://zh.wikipedia.org/wiki/词条名称
                    pendingLemma.setUrl(CrawlerConstants.WIKI_LINK_PREFIX + href);

                    pendingLemma.setStatus(LemmaStatusEnum.PENDING.getCode());

                    referenceLemmas.add(pendingLemma);
                    referenceIds.add(id);
                }
            }
        }

        // 删除引用标记和无用的数学表达式
        element.select("sup.reference, sup.noprint, annotation").remove();
        return new StringBuilder(element.text());
    }


    /**
     * 获取维基百科页面的ID
     *
     * @param title 维基百科页面的标题
     * @return 维基百科页面的curid，如果出错则返回0
     */
    public static int getID(String title) {
        int curid = 0;
        String apiUrl = "";
        try {
            // 代理设置
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CrawlerConstants.PROXYHOST, CrawlerConstants.PROXYPORT));

            String encodedTitle = URLEncoder.encode(title, "UTF-8");  // UnsupportedEncodingException IOException

            // 构造 API 请求 URL
            apiUrl = "https://zh.wikipedia.org/w/api.php?action=query&titles=" + encodedTitle + "&prop=info&inprop=url&format=json";

            // 创建 URL 和连接对象，并指定代理
            URI uri = new URI(apiUrl);
            URL url = uri.toURL();  // MalformedURLException
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);  // 使用代理连接  IOException
            connection.setRequestMethod("GET");  // ProtocolException IOException

            // 读取响应内容
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {  // IOException
                response.append(inputLine);
            }
            in.close();  // IOException

            // 解析 JSON 响应
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject pages = jsonResponse.getJSONObject("query").getJSONObject("pages");
            String pageId = pages.keys().next();  // 获取第一个页面的 ID
            curid = Integer.parseInt(pageId);  // 这是页面的 curid

        } catch (UnsupportedEncodingException e) {
            log.error("URL 编码失败，标题：{}，错误信息：{}", title, e.getMessage());
        } catch (URISyntaxException e) {
            log.error("URI 语法错误，URL：{}，错误信息：{}", apiUrl, e.getMessage());
        } catch (IOException e) {
            log.error("IO 异常，URL：{}，错误信息：{}", apiUrl, e.getMessage());
            return 0;
        } catch (Exception e) {
            log.error("未知异常，标题：{}，错误信息：{}", title, e.getMessage());
        }
        return curid;
    }

    /**
     * 将繁体中文转换为简体中文
     *
     * @param text 要转换的文本
     * @return 转换后的简体中文文本
     */
    public static String ConvertToSimplified(String text) {
        if (ZhConverterUtil.containsTraditional(text)) {
            text = ZhConverterUtil.toSimple(text);
        }
        return text;
    }
}
