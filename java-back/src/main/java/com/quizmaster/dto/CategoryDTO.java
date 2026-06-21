package com.quizmaster.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String icon;
    private Integer questionCount;
}