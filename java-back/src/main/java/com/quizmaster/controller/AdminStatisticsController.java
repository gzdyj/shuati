package com.quizmaster.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quizmaster.common.Result;
import com.quizmaster.dto.StatisticsDTO;
import com.quizmaster.entity.AnswerRecord;
import com.quizmaster.entity.Question;
import com.quizmaster.entity.User;
import com.quizmaster.mapper.AnswerRecordMapper;
import com.quizmaster.mapper.QuestionMapper;
import com.quizmaster.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @GetMapping("/overview")
    public Result<StatisticsDTO> overview() {
        StatisticsDTO dto = new StatisticsDTO();

        // 用户总数
        dto.setTotalUsers(userMapper.selectCount(null));

        // 题目总数
        dto.setTotalQuestions(questionMapper.selectCount(null));

        // 答题总数
        dto.setTotalAnswers(answerRecordMapper.selectCount(null));

        // 总正确率
        QueryWrapper<AnswerRecord> correctWrapper = new QueryWrapper<>();
        correctWrapper.eq("is_correct", 1);
        Long correctCount = answerRecordMapper.selectCount(correctWrapper);
        Long totalAnswers = dto.getTotalAnswers();
        if (totalAnswers > 0) {
            dto.setOverallCorrectRate((double) correctCount / totalAnswers * 100);
        } else {
            dto.setOverallCorrectRate(0.0);
        }

        // 今日活跃用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        QueryWrapper<AnswerRecord> todayWrapper = new QueryWrapper<>();
        todayWrapper.between("answer_time", todayStart, todayEnd);
        // 使用distinct查询今日答题的用户数
        Long activeUsersToday = answerRecordMapper.selectCount(todayWrapper);
        dto.setActiveUsersToday(activeUsersToday);

        return Result.success(dto);
    }
}