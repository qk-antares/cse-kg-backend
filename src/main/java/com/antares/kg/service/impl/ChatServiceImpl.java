package com.antares.kg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.dto.chat.ChatReq;
import com.antares.kg.model.enums.ChatStatusEnum;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.kg.model.entity.Chat;
import com.antares.kg.service.ChatService;
import com.antares.kg.mapper.ChatMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Antares
 * @description 针对表【chat(聊天对话表)】的数据库操作Service实现
 * @createDate 2024-12-16 09:11:54
 */
@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
        implements ChatService {
    @Value("${cse-kg.graph-rag.env-path}")
    private String ENV_PATH;

    @Value("${cse-kg.graph-rag.root-path}")
    private String ROOT_PATH;


    @Override
    public Chat chat(ChatReq chatReq) {
        Chat chat = BeanUtil.copyProperties(chatReq, Chat.class);
        save(chat);

        String[] fullCommand = {
                "powershell.exe", "-Command",
                String.format("%s/Scripts/graphrag", ENV_PATH), "query",
                "--root", ROOT_PATH,
                "--method", chatReq.getType(),
                "--query", String.format("\"%s\"", chatReq.getMsg())
        };

        try {
            // 使用ProcessBuilder启动
            ProcessBuilder pb = new ProcessBuilder(fullCommand);
            // 合并错误输出到标准输出
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), System.getProperty("sun.jnu.encoding")));
            // 用于保存结果
            StringBuilder res = new StringBuilder();
            StringBuilder log = new StringBuilder();
            // 标志是否进入需要提取的内容部分
            boolean flag = false;
            String line;

            while ((line = reader.readLine()) != null) {
                // 判断是否是目标内容的开始
                if (!flag && (line.startsWith("SUCCESS: Local Search Response:") || line.startsWith("SUCCESS: Global Search Response:"))) {
                    flag = true; // 开始提取内容
                    continue;
                }

                // 如果已经进入目标部分，追加内容
                if (flag) {
                    res.append(line).append(System.lineSeparator());
                } else {
                    log.append(line).append(System.lineSeparator());
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                chat.setStatus(ChatStatusEnum.FAILED.code);
                chat.setRes(log.toString());
            } else {
                chat.setStatus(ChatStatusEnum.SUCCUESS.code);
                chat.setRes(res.toString());
            }

            updateById(chat);

            return chat;
        } catch (Exception e) {
            log.error("GraphRAG检索出错：{}", e.getMessage());
            throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "GraphRAG检索失败");
        }
    }
}




