package com.antares.kg.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.antares.kg.constant.CrawlerConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LemmaScoreUtil {
    public static int scoreLemmaBySummary(String input) {
        // 构建请求体
        JSONObject requestBody = new JSONObject();

        requestBody.set("prompt", String.format(ResourceUtil.readUtf8Str(CrawlerConstants.PROMPT_PATH), input));
        requestBody.set("model", CrawlerConstants.OLLAMA_MODEL);
        requestBody.set("stream", false);

        // 发送POST请求
        try (HttpResponse response = HttpRequest.post(CrawlerConstants.OLLAMA_API)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute()) {
            String responseBody = response.body();
            JSONObject responseJson = JSONUtil.parseObj(responseBody);
            return Math.min(10, Integer.parseInt(responseJson.getStr("response")));
        } catch (Exception e) {
            log.error("获取词条分数失败：{}", e.getMessage());
            return -1;
        }
    }
}
