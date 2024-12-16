package com.antares.kg.controller;

import com.antares.kg.service.Neo4jService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/neo4j")
public class Neo4jController {
    @Resource
    private Neo4jService neo4jService;

    @GetMapping("/nodes")
    public Map<String, Object> getNodes(@RequestParam("name") String name){
        return neo4jService.getNodes(name);
    }

    @GetMapping("/all")
    public Map<String, Object> getAll(){
        return neo4jService.getAll();
    }
}
