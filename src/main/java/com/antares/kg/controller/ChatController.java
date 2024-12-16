package com.antares.kg.controller;

import com.antares.kg.model.dto.R;
import com.antares.kg.model.dto.chat.ChatReq;
import com.antares.kg.model.entity.Chat;
import com.antares.kg.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    private ChatService chatService;

    @PostMapping
    public R<Chat> chat(@RequestBody ChatReq chatReq) {
        Chat response = chatService.chat(chatReq);
        return R.ok(response);
    }
}
