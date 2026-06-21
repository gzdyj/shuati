package com.quizmaster.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quizmaster.entity.QuestionOption;
import com.quizmaster.mapper.QuestionOptionMapper;
import com.quizmaster.service.QuestionOptionService;
import org.springframework.stereotype.Service;

@Service
public class QuestionOptionServiceImpl extends ServiceImpl<QuestionOptionMapper, QuestionOption> implements QuestionOptionService {
}
