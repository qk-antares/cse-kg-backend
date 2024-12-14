package com.antares.kg.service.impl;


import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.service.Neo4jService;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class Neo4jServiceImpl implements Neo4jService {
    @Resource
    private Driver driver;

    @Override
    public Map<String, Object> getNodes(String name) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> nodes = new HashMap<>();
        List<Object> links = new ArrayList<>();
        try (Session session = driver.session()) {
            session.executeRead(tx -> {
                Result result = tx.run(getUri("name",name));
                if(!result.hasNext())result = tx.run(getUri("title",name));
                if(!result.hasNext())result = tx.run(getUri("summary",name));
                while (result.hasNext()) {
                    Record record = result.next();
                    if(!record.get("n").isNull()){
                        Node n = record.get("n").asNode();
                        nodes.put(n.elementId(), parseNode(n));
                    }
                    if(!record.get("k").isNull()){
                        Node k = record.get("k").asNode();
                        nodes.put(k.elementId(), parseNode(k));
                    }
                    if(!record.get("m").isNull()){
                        Node m = record.get("m").asNode();
                        nodes.put(m.elementId(), parseNode(m));
                    }

                    if (!record.get("r1").isNull()) {
                        Relationship r1 = record.get("r1").asRelationship();
                        links.add(parseRelationship(r1));
                    }
                    if (!record.get("r2").isNull()) {
                        Relationship r2 = record.get("r2").asRelationship();
                        links.add(parseRelationship(r2));
                    }
                    if (!record.get("r").isNull()) {
                        Relationship r = record.get("r").asRelationship();
                        links.add(parseRelationship(r));
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Neo4J查询出错：{}", e.getMessage());
            throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "Neo4J查询失败");
        }
        List<Object> list = new ArrayList<>(nodes.values());
        resultMap.put("nodes", list);
        resultMap.put("links", links);
        return resultMap;
    }

    public Map<String, String> parseNode(Node node) {
        Map<String, String> map = new HashMap<>();
        String name = "";
        String description = "";
        if (!node.get("name").isNull()) {
            name = node.get("name").asString();
        } else if (!node.get("title").isNull()) {
            name = node.get("title").asString();
        } else if (!node.get("summary").isNull()) {
            name = node.get("summary").asString();
        } else if (!node.get("n_tokens").isNull()) {
            name = node.get("n_tokens").toString();
        }

        if (!node.get("description").isNull()) {
            description = node.get("description").asString();
        } else if (!node.get("full_content").isNull()) {
            description = node.get("full_content").asString();
        } else if (!node.get("explanation").isNull()) {
            description = node.get("explanation").asString();
        } else if (!node.get("text").isNull()) {
            description = node.get("text").asString();
        }

        map.put("id", node.elementId());
        map.put("name", name);
        map.put("description", description);
        map.put("label", node.labels().iterator().next());

        return map;
    }

    public String getUri(String attr, String value) {
        return String.format("match (n)-[r]-(m) where n.%s contains \"%s\" " +
                "optional match (m)-[r1]-(k) where k in [(n)-[]-(x)|x]" +
                "optional match (n)-[r2]-(k)" +
                "return distinct n,r,m,r1,k,r2 limit 50", attr, value);
    }

    public Map<String, String> parseRelationship(Relationship r) {
        Map<String, String> map = new HashMap<>();
        map.put("source", r.startNodeElementId());
        map.put("target", r.endNodeElementId());
        map.put("type", r.type());

        return map;

    }
}