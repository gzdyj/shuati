package com.quizmaster.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String content;
    private List<OptionDTO> options;
    private String explanation;

    @Data
    public static class OptionDTO {
        private String id;
        private String content;
    }
}
