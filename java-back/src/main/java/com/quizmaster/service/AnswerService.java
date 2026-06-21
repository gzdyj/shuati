package com.quizmaster.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quizmaster.dto.SubmitAnswerDTO;
import com.quizmaster.entity.AnswerRecord;
import com.quizmaster.entity.Question;
import com.quizmaster.mapper.AnswerRecordMapper;
import com.quizmaster.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnswerService extends ServiceImpl<AnswerRecordMapper, AnswerRecord> {

    @Autowired
    private QuestionMapper questionMapper;

    public Map<String, Object> submitAnswer(Long userId, SubmitAnswerDTO dto) {
        List<AnswerRecord> records = new ArrayList<>();
        int correctCount = 0;

        for (Map.Entry<Long, String> entry : dto.getAnswers().entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();

            Question question = questionMapper.selectById(questionId);
            if (question == null) continue;

            boolean isCorrect = userAnswer.equals(question.getCorrectAnswer());
            if (isCorrect) correctCount++;

            AnswerRecord record = new AnswerRecord();
            record.setUserId(userId);
            record.setQuestionId(questionId);
            record.setUserAnswer(userAnswer);
            record.setCorrectAnswer(question.getCorrectAnswer());
            record.setIsCorrect(isCorrect);
            record.setAnswerTime(LocalDateTime.now());

            records.add(record);
        }

        this.saveBatch(records);

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", records.size());
        result.put("correctCount", correctCount);
        result.put("correctRate", records.size() > 0 ? (correctCount * 100 / records.size()) : 0);

        return result;
    }

    public List<Map<String, Object>> getHistory(Long userId) {
        // TODO: 实现从数据库查询历史记录
        return new ArrayList<>();
    }

    public List<Map<String, Object>> getWrongQuestions(Long userId) {
        // TODO: 实现从数据库查询错题
        return new ArrayList<>();
    }
}
