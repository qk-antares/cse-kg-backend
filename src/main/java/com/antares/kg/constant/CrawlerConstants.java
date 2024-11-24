package com.antares.kg.constant;

public interface CrawlerConstants {
    String OLLAMA_API = "http://localhost:11434/api/generate";
    String PROMPT_PATH = "prompt/prompt.txt";
    String OLLAMA_MODEL = "mistral:10k";

    String BAIKE_LINK_PREFIX = "https://baike.baidu.com";
    int BAIKE_LINK_SUFFIX_LENGTH = "?fromModule=lemma_inlink".length();

    String BAIKE_MAIN_CONTENT_SELECTOR = "div[class^=contentTab_Wa7Hh]";
    String BAIKE_SUMMARY_SELECTOR = "div[class^=lemmaSummary_xoHAz J-summary]";
    String BAIKE_TITLE_SELECTOR = "div[class^=paraTitle_MDWuj]";
    String BAIKE_PARA_SELECTOR = "div[class^=para_yeVDI]";
    String BAIKE_DETAIL_SELECTOR = "div[class^=J-lemma-content]";

    String BAIKE_TITLE_CLASS="paraTitle_MDWuj";
    String BAIKE_PARA_CLASS="para_yeVDI";
}