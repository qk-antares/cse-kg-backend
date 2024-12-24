package com.antares.kg.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalSearchTest {
    @Test
    public void localSearchPrompt(){
        // 文件路径
        String filePath = "src/test/java/com/antares/kg/service/localSearchPrompt_raw.txt";

        try {
            // 读取文件内容
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // 将文本中的 "\n" 字符替换为真正的换行符
            String modifiedContent = content.replace("\\n", System.lineSeparator());

            // 如果需要，可以将替换后的内容保存到新文件
            Files.write(Paths.get("localSearchPrompt.txt"), modifiedContent.getBytes());
            System.out.println("\n替换后的内容已保存到 modified_example.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
