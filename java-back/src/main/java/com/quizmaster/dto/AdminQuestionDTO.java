package com.quizmaster.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdminQuestionDTO {
    private Long id;
    private String content;
    private String correctAnswer;
    private Long categoryId;
    private String categoryName;
    private String explanation;
    private String createTime;
    private List<OptionDTO> options;

    @Data
    public static class OptionDTO {
        private String optionId;
        private String content;
    }
}