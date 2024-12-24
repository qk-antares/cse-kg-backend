package com.antares.kg.config;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.antares.kg.model.dto.chat.Entity;
import com.antares.kg.model.dto.chat.Relationship;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatasourceConfig {
    @Bean(name = "entities")
    public List<Entity> loadEntities(){
        // 读取 JSON 文件
        String jsonContent = ResourceUtil.readUtf8Str("json/entities.json");

        // 使用 Hutool 解析 JSON
        JSONArray jsonArray = JSONUtil.parseArray(jsonContent);

        // 转换为实体类对象列表
        List<Entity> entities = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;

            Entity entity = new Entity();
            entity.setId(jsonObject.getInt("human_readable_id"));
            entity.setTitle(jsonObject.getStr("title"));
            entity.setType(jsonObject.getStr("type"));
            entity.setDescription(jsonObject.getStr("description"));

            entities.add(entity);
        }

        return entities;
    }

    @Bean(name = "relationships")
    public List<Relationship> loadRelationships(){
        String jsonContent = ResourceUtil.readUtf8Str("json/relationships.json");
        JSONArray jsonArray = JSONUtil.parseArray(jsonContent);

        List<Relationship> relationships = new ArrayList<>();

        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;

            Relationship relationship = new Relationship();
            relationship.setId(jsonObject.getInt("human_readable_id"));
            relationship.setSource(jsonObject.getStr("source"));
            relationship.setTarget(jsonObject.getStr("target"));
            relationship.setDescription(jsonObject.getStr("description"));
            relationship.setWeight(jsonObject.getDouble("weight"));

            relationships.add(relationship);
        }

        return relationships;
    }
}
