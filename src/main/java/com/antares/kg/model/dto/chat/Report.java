package com.antares.kg.model.dto.chat;

import lombok.Data;

import java.util.List;

@Data
public class Report {
    private Integer id;
    private Integer level;
    private String title;
    private String summary;
    private Double rating;
    private String ratingExplanation;
    private List<Finding> findings;
}
