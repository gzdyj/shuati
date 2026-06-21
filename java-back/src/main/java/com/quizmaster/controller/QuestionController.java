package com.quizmaster.controller;

import com.quizmaster.common.Result;
import com.quizmaster.dto.QuestionDTO;
import com.quizmaster.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/random")
    public Result<List<QuestionDTO>> getRandomQuestions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {

        List<QuestionDTO> questions;
        if (categoryId != null) {
            questions = questionService.getRandomQuestionsByCategory(categoryId, count);
        } else {
            questions = questionService.getRandomQuestions(count);
        }
        return Result.success(questions);
    }
}
