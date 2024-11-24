package com.antares.kg;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;

public class OllamaTest {
    /**
     * 调用OLLAMA大模型接口
     *
     * @param input 用户输入的字符串
     * @return 大模型的输出
     */
    public static String callOllamaModel(String input) {
        // OLLAMA接口的URL，替换为你的实际URL
        String url = "http://localhost:11434/api/generate";

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("prompt", input);
        requestBody.set("model", "mistral:10k");
        requestBody.set("stream", false);

        try {
            // 发送POST请求
            String response = HttpRequest.post(url)
                    .header("Content-Type", "application/json") // 设置请求头
                    .body(requestBody.toString())              // 设置请求体
                    .execute()                                 // 执行请求
                    .body();                                   // 获取响应体

            // 解析响应体（假设响应体是JSON）
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getStr("response"); // 假设输出字段是"output"
        } catch (Exception e) {
            // 捕获异常并打印错误信息
            System.err.println("调用OLLAMA接口失败：" + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        // 测试调用
        String input = """
你是计算机科学与技术领域的专家，下面我将给你一些名词的概念，你需要根据概念的解释判断它与计算机科学与技术的直接相关性分数。
当该概念与计算机科学存在直接联系时（例如概念的解释中包含了一定数量的计算机领域名词），你应该给6-10分；
当该概念与计算机科学仅存在间接联系时（例如概念的解释仅包含了个别的计算机领域名次），你应该给1-5分；
当该概念与计算机科学不存在直接联系时（例如概念的解释完全不包含计算机领域名词），你应该给0分；
上述的计算机领域名词可以是与数据结构、计算机网络、操作系统、机器学习、算法、软件工程等相关的。你应该仅输出一个0到10的整型分数，且不要做任何解释。

下面是我本次的提问：
输入：天文学（Astronomy）是研究宇宙空间天体、宇宙的结构和发展的学科。内容包括天体的构造、性质和运行规律等。天文学是一门古老的科学，自有人类文明史以来，天文学就有重要的地位。
                   主要通过观测天体发射到地球的辐射，发现并测量它们的位置、探索它们的运动规律、研究它们的物理性质、化学组成、内部结构、能量来源及其演化规律。 [1]
                   天文学的起源与文明的起源大致处于同一时期。有关天文作为文明之源的思考，古人理解得相当深刻。《尚书·舜典》：“浚哲文明，温恭允塞。”《易·乾·文言》：“见龙在田，天下文明。”这些认识从根本上建立了天文与人文的固有联系。 [3]
                   在天文学悠久的历史中，随着研究方法的改进及发展，先后创立了天体测量学、天体力学和天体物理学。
                   输出：？
       """;
        String output = callOllamaModel(input);
        System.out.println("大模型输出: " + output);
    }
}
