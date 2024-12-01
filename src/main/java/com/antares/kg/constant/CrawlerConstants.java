package com.antares.kg.constant;

public interface CrawlerConstants {
    String OLLAMA_API = "http://localhost:11434/api/generate";
    String PROMPT_PATH = "prompt/prompt.txt";
    String OLLAMA_MODEL = "mistral:10k";

    String BAIKE_LINK_PATTERN = "/item/(.*?)/(\\d+)";
    String BAIKE_LINK_PREFIX = "https://baike.baidu.com";
    String BAIKE_URL_PATTERN = "^https://baike\\.baidu\\.com/item/[^/]+/\\d+$";
    int BAIKE_LINK_SUFFIX_LENGTH = "?fromModule=lemma_inlink".length();
    int BAIKE_TITLE_SUFFIX_LENGTH = "_百度百科".length();

    String BAIKE_MAIN_CONTENT_SELECTOR = "div[class^=contentTab_Wa7Hh]";
    String BAIKE_SUMMARY_SELECTOR = "div[class^=lemmaSummary_xoHAz J-summary]";
    String BAIKE_TITLE_SELECTOR = "div[class^=paraTitle_MDWuj]";
    String BAIKE_PARA_SELECTOR = "div[class^=para_yeVDI]";
    String BAIKE_DETAIL_SELECTOR = "div[class^=J-lemma-content]";

    String BAIKE_TITLE_CLASS="paraTitle_MDWuj";
    String BAIKE_PARA_CLASS="para_yeVDI";


    String WIKI_LINK_PREFIX = "https://zh.wikipedia.org";
    String WIKI_LINK_PATTERN = "^/wiki/(?![A-Z][a-zA-Z]*:)[^/]+$";
    String WIKI_URL_PATTERN = "^https://zh.wikipedia.org/wiki/(?![A-Z][a-zA-Z]*:)[^/]+$";
    int WIKI_TITLE_SUFFIX_LENGTH = " - 维基百科，自由的百科全书".length();
    int WIKI_PREFIX_LENGTH = "/wiki/".length();

    String WIKI_MAIN_CONTENT_SELECTOR = "div.mw-content-ltr.mw-parser-output";
    String WIKI_TITLE_SELECTOR = "div.mw-heading";
    String WIKI_PARA_SELECTOR = "p";

    String WIKI_TITLE_CLASS = "mw-heading";

    String PROXYHOST = "127.0.0.1";  // TODO 更改为可用的代理服务器地址
    int PROXYPORT = 7890;  // TODO 更改为可用的代理服务器端口
}
