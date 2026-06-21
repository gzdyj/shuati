package com.quizmaster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quizmaster.entity.QuestionOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionOptionMapper extends BaseMapper<QuestionOption> {

    @Select("SELECT * FROM question_option WHERE question_id = #{questionId}")
    List<QuestionOption> getOptionsByQuestionId(@Param("questionId") Long questionId);

    void insertBatch(@Param("list") List<QuestionOption> list);
}
