package com.quizmaster.controller;

import com.quizmaster.common.Result;
import com.quizmaster.dto.SubmitAnswerDTO;
import com.quizmaster.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAnswer(
            Authentication authentication,
            @RequestBody SubmitAnswerDTO dto) {

        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> result = answerService.submitAnswer(userId, dto);
        return Result.success(result);
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<Map<String, Object>> history = answerService.getHistory(userId);
        return Result.success(history);
    }
}
