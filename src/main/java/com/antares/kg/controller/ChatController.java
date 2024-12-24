package com.antares.kg.controller;

import com.antares.kg.mapper.EntityMapper;
import com.antares.kg.mapper.RelationshipMapper;
import com.antares.kg.model.dto.R;
import com.antares.kg.model.dto.chat.ChatReq;
import com.antares.kg.model.entity.Chat;
import com.antares.kg.model.entity.Entity;
import com.antares.kg.model.entity.Relationship;
import com.antares.kg.service.ChatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    private ChatService chatService;
    @Resource
    private EntityMapper entityMapper;
    @Resource
    private RelationshipMapper relationshipMapper;

    @PostMapping
    public R<Chat> chat(@RequestBody ChatReq chatReq) {
        Chat response = chatService.chat(chatReq);
        return R.ok(response);
    }

    @GetMapping("/entity/{id}")
    public R<Entity> getEntity(@PathVariable Long id) {
        Entity entity = entityMapper.selectById(id);
        return R.ok(entity);
    }

    @GetMapping("/relationship/{id}")
    public R<Relationship> getRelationship(@PathVariable Long id) {
        Relationship relationship = relationshipMapper.selectById(id);
        return R.ok(relationship);
    }
}
