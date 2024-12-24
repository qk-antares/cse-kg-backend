package com.antares.kg.service;

import com.antares.kg.model.dto.chat.ChatReq;
import com.antares.kg.model.entity.Chat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ChatServiceTest {
    @Resource
    private ChatService chatService;

    @Test
    public void chat() {
        ChatReq chatReq = new ChatReq();
        chatReq.setType("local");
        chatReq.setMsg("计算机科学专业需要学习哪些课程?");

        Chat chat = chatService.chat(chatReq);
        System.out.println(chat);
    }
}
