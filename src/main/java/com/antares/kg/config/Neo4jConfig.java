package com.antares.kg.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置Neo4J连接
 */
@Configuration
public class Neo4jConfig {
    @Value("${cse-kg.neo4j.uri}")
    private String uri;

    @Value("${cse-kg.neo4j.username}")
    private String username;

    @Value("${cse-kg.neo4j.password}")
    private String password;

    @Bean
    public Driver driver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }
}
