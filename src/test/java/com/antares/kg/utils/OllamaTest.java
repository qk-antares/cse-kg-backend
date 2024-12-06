package com.antares.kg.utils;

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
                当该概念与计算机科学存在直接联系时（例如概念的解释中包含了一定数量的计算机领域名词），你应该给6到10分；
                当该概念与计算机科学仅存在间接联系时（例如概念的解释仅包含了个别的计算机领域名次），你应该给1到5分；
                当该概念与计算机科学不存在直接联系时（例如概念的解释完全不包含计算机领域名词），你应该给0分；
                上述的计算机领域名词可以是与数据结构、计算机网络、操作系统、机器学习、算法、软件工程等相关的。
                注意：你应该仅输出一个0到10的整型分数，且不要做任何解释。
                
                下面是我本次的提问：
                输入：大语言模型（Large Language Model，简称LLM），指使用大量文本数据训练的深度学习模型，可以生成自然语言文本或理解语言文本的含义。大语言模型可以处理多种自然语言任务，如文本分类、问答、对话等，是通向人工智能的重要途径。目前大语言模型采用与小模型类似的Transformer架构和预训练目标（如 Language Modeling），与小模型的区别是增加模型大小、训练数据和计算资源。
                20世纪40年代末和50年代，计算机技术开始被用于研究和处理自然语言。1950年，图灵测试问世。1954年，乔治・戴沃尔设计出首台可编程机器人。1956年，达特茅斯学院举办了历史上首次人工智能研讨会，标志人工智能诞生。2020年1月23日，OpenAI发表了论文《Scaling Laws for Neural Language Models》，研究基于交叉熵损失的语言模型性能的经验尺度法则；11月30日，OpenAI公司发布ChatGPT，迅速引起社会各界关注。ChatGPT属于一类基于GPT技术的大语言模型。Google、Microsoft、NVIDIA等公司也给出了自己的大语言模型。2024年3月，马斯克的xAI公司正式发布大模型Grok-1，参数量达到3140亿，超OpenAI GPT-3.5的1750亿。
                2023年12月26日，大语言模型入选“2023年度十大科技名词”。2024年4月，第27届联合国科技大会，世界数字技术院发布了《生成式人工智能应用安全测试标准》和《大语言模型安全测试方法》两项国际标准，由OpenAI、蚂蚁集团、科大讯飞、谷歌、微软、英伟达、百度、腾讯等数十家单位多名专家学者共同编制而成。
                
                输出（0到10的整型分数）：？
                """;

        String output = callOllamaModel(input);
        System.out.println("大模型输出: " + output);
    }
}
