package com.antares.kg.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.antares.kg.mapper.EntityMapper;
import com.antares.kg.model.entity.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class EntityServiceTest {
    @Resource
    private EntityMapper entityMapper;

    @Test
    public void loadEntities() {
        entityMapper.delete(null);

        try {
            // 读取 JSON 文件
            String jsonContent = ResourceUtil.readUtf8Str("json/entities.json");

            // 使用 Hutool 解析 JSON
            JSONArray jsonArray = JSONUtil.parseArray(jsonContent);

            // 转换为实体类对象列表
            List<Entity> entities = new ArrayList<>();
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                Entity entity = new Entity();
                entity.setId(Long.parseLong(jsonObject.getStr("human_readable_id")));
                entity.setTitle(jsonObject.getStr("title"));
                entity.setType(jsonObject.getStr("type"));
                entity.setDescription(jsonObject.getStr("description"));

                entities.add(entity);
            }

            // 批量保存到数据库
            entityMapper.insert(entities);

            System.out.println("JSON 数据已成功导入数据库！");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
