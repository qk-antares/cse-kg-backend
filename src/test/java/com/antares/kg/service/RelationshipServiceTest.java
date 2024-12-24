package com.antares.kg.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.antares.kg.mapper.RelationshipMapper;
import com.antares.kg.model.dto.chat.Relationship;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RelationshipServiceTest {
    @Resource
    private RelationshipMapper relationshipMapper;

    @Test
    public void test() {
        relationshipMapper.delete(null);

        // 读取 JSON 文件
        String jsonContent = ResourceUtil.readUtf8Str("json/relationships.json");

        // 使用 Hutool 解析 JSON
        JSONArray jsonArray = JSONUtil.parseArray(jsonContent);

        // 转换为实体类对象列表
        List<Relationship> relationships = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;

            Relationship relationship = new Relationship();
            relationship.setId(Long.parseLong(jsonObject.getStr("human_readable_id")));
            relationship.setSource(jsonObject.getStr("source"));
            relationship.setTarget(jsonObject.getStr("target"));
            relationship.setDescription(jsonObject.getStr("description"));
            relationship.setWeight(jsonObject.getDouble("weight"));

            relationships.add(relationship);
        }

        // 批量保存到数据库
        relationshipMapper.insert(relationships);

        System.out.println("JSON 数据已成功导入数据库！");
    }
}
