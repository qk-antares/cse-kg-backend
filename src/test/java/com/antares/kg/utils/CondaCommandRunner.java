package com.antares.kg.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CondaCommandRunner {
    public static void main(String[] args) {
        // 要运行的指令h
        String command = "graphrag --help";

        try {
            String[] fullCommand = {
                    "powershell.exe", "-Command", "D:\\anaconda3\\envs\\graphrag\\Scripts\\graphrag query --root E:\\Workplace\\GraphPro\\backup\\ragtest-v1 --method local --query \"计算机科学专业需要学习哪些课程?\""
            };

            // 使用ProcessBuilder启动
            ProcessBuilder pb = new ProcessBuilder(fullCommand);
            pb.redirectErrorStream(true); // 合并错误输出到标准输出
            Process process = pb.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), System.getProperty("sun.jnu.encoding")));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
