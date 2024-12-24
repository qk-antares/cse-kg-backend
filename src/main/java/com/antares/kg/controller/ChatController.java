package com.antares.kg.controller;

import com.antares.kg.model.dto.R;
import com.antares.kg.model.dto.chat.ChatReq;
import com.antares.kg.model.dto.chat.ReferenceReq;
import com.antares.kg.model.entity.Chat;
import com.antares.kg.model.dto.chat.Entity;
import com.antares.kg.model.dto.chat.Relationship;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.service.ChatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    private ChatService chatService;
    @Resource(name = "entities")
    private List<Entity> entities;
    @Resource(name = "relationships")
    private List<Relationship> relationships;


    @PostMapping
    public R<Chat> chat(@RequestBody ChatReq chatReq) {
        Chat response = chatService.chat(chatReq);
        return R.ok(response);
    }

    @PostMapping("/reference")
    public R getEntity(@RequestBody ReferenceReq referenceReq) {
        Integer id = referenceReq.getId();
        try {
            return switch (referenceReq.getType()) {
                case "entity" -> R.ok(entities.get(id));
                case "relationship" -> R.ok(relationships.get(id));
                default -> R.error(HttpCodeEnum.PARAMS_ERROR, "不支持的引用类型");
            };
        } catch (IndexOutOfBoundsException e) {
            return R.error(HttpCodeEnum.PARAMS_ERROR, "不存在的引用id");
        }
    }
}
