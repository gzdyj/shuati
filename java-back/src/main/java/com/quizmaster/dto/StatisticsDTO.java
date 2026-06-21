package com.quizmaster.dto;

import lombok.Data;

@Data
public class StatisticsDTO {
    private Long totalUsers;
    private Long totalQuestions;
    private Long totalAnswers;
    private Double overallCorrectRate;
    private Long activeUsersToday;
}