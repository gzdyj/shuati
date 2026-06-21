package com.quizmaster.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quizmaster.dto.QuestionDTO;
import com.quizmaster.entity.Question;
import com.quizmaster.entity.QuestionOption;
import com.quizmaster.mapper.QuestionMapper;
import com.quizmaster.mapper.QuestionOptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService extends ServiceImpl<QuestionMapper, Question> {

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    private static final int DEFAULT_QUESTION_COUNT = 10;

    public List<QuestionDTO> getRandomQuestions(Integer count) {
        int limit = count != null ? count : DEFAULT_QUESTION_COUNT;
        List<Question> questions = baseMapper.randomQuestions(limit);
        return convertToDTO(questions);
    }

    public List<QuestionDTO> getRandomQuestionsByCategory(Long categoryId, Integer count) {
        int limit = count != null ? count : DEFAULT_QUESTION_COUNT;
        List<Question> questions = baseMapper.randomQuestionsByCategory(categoryId, limit);
        return convertToDTO(questions);
    }

    private List<QuestionDTO> convertToDTO(List<Question> questions) {
        return questions.stream().map(q -> {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(q.getId());
            dto.setContent(q.getContent());
            dto.setExplanation(q.getExplanation());

            List<QuestionOption> options = questionOptionMapper.getOptionsByQuestionId(q.getId());
            List<QuestionDTO.OptionDTO> optionDTOs = options.stream().map(opt -> {
                QuestionDTO.OptionDTO optDTO = new QuestionDTO.OptionDTO();
                optDTO.setId(opt.getOptionId());
                optDTO.setContent(opt.getContent());
                return optDTO;
            }).collect(Collectors.toList());

            dto.setOptions(optionDTOs);
            return dto;
        }).collect(Collectors.toList());
    }
}
