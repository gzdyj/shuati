package com.quizmaster.dto;

import lombok.Data;

@Data
public class QuestionImportDTO {
    private Integer rowNum;
    private String title;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String analysis;
}