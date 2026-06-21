package com.quizmaster.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitAnswerDTO {
    private Map<Long, String> answers;
}
