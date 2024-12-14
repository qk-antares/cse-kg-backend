package com.antares.kg.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

@SpringBootTest
public class Neo4JServiceTest {
    @Resource
    private Neo4jService neo4jService;

    @Test
    public void test() {
        Map<String, Object> res = neo4jService.getNodes("计算机科学");
        System.out.println(res);
    }
}
