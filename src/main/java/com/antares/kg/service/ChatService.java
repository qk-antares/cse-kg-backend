package com.antares.kg.service;

import com.antares.kg.model.dto.chat.ChatReq;
import com.antares.kg.model.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Antares
* @description 针对表【chat(聊天对话表)】的数据库操作Service
* @createDate 2024-12-16 09:11:54
*/
public interface ChatService extends IService<Chat> {
    Chat chat(ChatReq chatReq);
}
