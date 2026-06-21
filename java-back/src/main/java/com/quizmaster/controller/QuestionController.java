package com.quizmaster.controller;

import com.quizmaster.common.Result;
import com.quizmaster.dto.CategoryDTO;
import com.quizmaster.dto.QuestionDTO;
import com.quizmaster.entity.Category;
import com.quizmaster.mapper.CategoryMapper;
import com.quizmaster.mapper.QuestionMapper;
import com.quizmaster.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private QuestionMapper questionMapper;

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

    @GetMapping("/categories")
    public Result<List<CategoryDTO>> getCategories() {
        List<Category> categories = categoryMapper.selectList(null);
        List<CategoryDTO> dtoList = categories.stream().map(category -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setIcon(category.getIcon());
            dto.setQuestionCount(category.getQuestionCount());
            return dto;
        }).collect(Collectors.toList());
        return Result.success(dtoList);
    }
}
