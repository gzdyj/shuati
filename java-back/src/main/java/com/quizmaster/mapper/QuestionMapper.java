package com.quizmaster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quizmaster.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("SELECT * FROM question ORDER BY RAND() LIMIT #{limit}")
    List<Question> randomQuestions(@Param("limit") int limit);

    @Select("SELECT * FROM question WHERE category_id = #{categoryId} ORDER BY RAND() LIMIT #{limit}")
    List<Question> randomQuestionsByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    void insertBatch(@Param("list") List<Question> list);
}
