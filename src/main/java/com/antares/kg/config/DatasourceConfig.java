package com.antares.kg.config;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.antares.kg.model.dto.chat.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        JSONArray relationships = JSONUtil.parseArray(jsonContent);

        List<Relationship> res = new ArrayList<>();

        for (Object obj : relationships) {
            JSONObject jsonObject = (JSONObject) obj;

            Relationship relationship = new Relationship();
            relationship.setId(jsonObject.getInt("human_readable_id"));
            relationship.setSource(jsonObject.getStr("source"));
            relationship.setTarget(jsonObject.getStr("target"));
            relationship.setDescription(jsonObject.getStr("description"));
            relationship.setWeight(jsonObject.getDouble("weight"));

            res.add(relationship);
        }

        return res;
    }

    @Bean(name = "sources")
    public List<Source> loadSources(){
        String jsonContent = ResourceUtil.readUtf8Str("json/sources.json");
        JSONArray sources = JSONUtil.parseArray(jsonContent);

        List<Source> res = new ArrayList<>();

        for (Object obj : sources) {
            JSONObject jsonObject = (JSONObject) obj;

            Source source = new Source();
            source.setId(jsonObject.getInt("human_readable_id") - 1);
            source.setText(jsonObject.getStr("text"));
            source.setNTokens(jsonObject.getInt("n_tokens"));

            res.add(source);
        }

        return res;
    }

    @Bean(name = "reports")
    public Map<Integer, Report> loadReports(){
        String jsonContent = ResourceUtil.readUtf8Str("json/reports.json");
        JSONArray reports = JSONUtil.parseArray(jsonContent);

        Map<Integer, Report> res = new HashMap<Integer, Report>();

        for (Object obj : reports) {
            JSONObject jsonObject = (JSONObject) obj;

            Report report = new Report();
            report.setId(jsonObject.getInt("human_readable_id"));
            report.setLevel(jsonObject.getInt("level"));

            JSONObject fullContent = JSONUtil.parseObj(jsonObject.get("full_content_json"));

            report.setTitle(fullContent.getStr("title"));
            report.setSummary(fullContent.getStr("summary"));
            report.setRating(fullContent.getDouble("rating"));
            report.setRatingExplanation(fullContent.getStr("rating_explanation"));
            report.setFindings(JSONUtil.toList(fullContent.getStr("findings"), Finding.class));

            res.put(report.getId(), report);
        }

        return res;
    }

}
