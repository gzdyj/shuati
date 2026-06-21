package com.quizmaster.dto;

import lombok.Data;

@Data
public class AdminUserDTO {
    private Long id;
    private String username;
    private String role;
    private String createTime;
    private Integer totalAnswered;
    private Integer correctCount;
    private Double correctRate;
}