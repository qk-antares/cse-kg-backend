package com.antares.kg.service;

import java.util.Map;

public interface Neo4jService {
    Map<String, Object> getNodes(String name);
}
