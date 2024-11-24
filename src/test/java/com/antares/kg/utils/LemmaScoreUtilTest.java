package com.antares.kg.utils;

import org.junit.jupiter.api.Test;

public class LemmaScoreUtilTest {
    @Test
    public void test() {
        int i = LemmaScoreUtil.scoreLemmaBySummary("天文学（Astronomy）是研究宇宙空间天体、宇宙的结构和发展的学科。内容包括天体的构造、性质和运行规律等。天文学是一门古老的科学，自有人类文明史以来，天文学就有重要的地位。");
        System.out.println(i);
    }
}
