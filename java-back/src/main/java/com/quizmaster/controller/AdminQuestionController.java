package com.quizmaster.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quizmaster.common.Result;
import com.quizmaster.dto.AdminQuestionDTO;
import com.quizmaster.entity.Category;
import com.quizmaster.entity.Question;
import com.quizmaster.entity.QuestionOption;
import com.quizmaster.mapper.CategoryMapper;
import com.quizmaster.mapper.QuestionMapper;
import com.quizmaster.mapper.QuestionOptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public Result<Page<AdminQuestionDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {

        Page<Question> questionPage = new Page<>(page, size);
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("content", keyword);
        }

        questionMapper.selectPage(questionPage, wrapper);

        List<AdminQuestionDTO> dtoList = questionPage.getRecords().stream().map(question -> {
            AdminQuestionDTO dto = convertToDTO(question);
            return dto;
        }).collect(Collectors.toList());

        Page<AdminQuestionDTO> resultPage = new Page<>(page, size);
        resultPage.setRecords(dtoList);
        resultPage.setTotal(questionPage.getTotal());

        return Result.success(resultPage);
    }

    @GetMapping("/{id}")
    public Result<AdminQuestionDTO> detail(@PathVariable Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error(404, "题目不存在");
        }

        AdminQuestionDTO dto = convertToDTO(question);
        return Result.success(dto);
    }

    @PostMapping
    @Transactional
    public Result<Void> create(@RequestBody AdminQuestionDTO dto) {
        Question question = new Question();
        question.setContent(dto.getContent());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setCategoryId(dto.getCategoryId());
        question.setExplanation(dto.getExplanation());

        questionMapper.insert(question);

        // 保存选项
        if (dto.getOptions() != null) {
            for (AdminQuestionDTO.OptionDTO optionDTO : dto.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionId(optionDTO.getOptionId());
                option.setContent(optionDTO.getContent());
                questionOptionMapper.insert(option);
            }
        }

        // 更新分类题目数量
        if (dto.getCategoryId() != null) {
            updateCategoryQuestionCount(dto.getCategoryId());
        }

        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminQuestionDTO dto) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error(404, "题目不存在");
        }

        question.setContent(dto.getContent());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setCategoryId(dto.getCategoryId());
        question.setExplanation(dto.getExplanation());

        questionMapper.updateById(question);

        // 删除旧选项
        QueryWrapper<QuestionOption> wrapper = new QueryWrapper<>();
        wrapper.eq("question_id", id);
        questionOptionMapper.delete(wrapper);

        // 保存新选项
        if (dto.getOptions() != null) {
            for (AdminQuestionDTO.OptionDTO optionDTO : dto.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(id);
                option.setOptionId(optionDTO.getOptionId());
                option.setContent(optionDTO.getContent());
                questionOptionMapper.insert(option);
            }
        }

        // 更新分类题目数量
        if (dto.getCategoryId() != null) {
            updateCategoryQuestionCount(dto.getCategoryId());
        }

        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error(404, "题目不存在");
        }

        // 删除选项
        QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
        optionWrapper.eq("question_id", id);
        questionOptionMapper.delete(optionWrapper);

        // 删除题目
        questionMapper.deleteById(id);

        // 更新分类题目数量
        if (question.getCategoryId() != null) {
            updateCategoryQuestionCount(question.getCategoryId());
        }

        return Result.success("删除成功", null);
    }

    @DeleteMapping("/batch")
    @Transactional
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的题目");
        }

        for (Long id : ids) {
            Question question = questionMapper.selectById(id);
            if (question != null) {
                // 删除选项
                QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
                optionWrapper.eq("question_id", id);
                questionOptionMapper.delete(optionWrapper);

                // 删除题目
                questionMapper.deleteById(id);

                // 更新分类题目数量
                if (question.getCategoryId() != null) {
                    updateCategoryQuestionCount(question.getCategoryId());
                }
            }
        }

        return Result.success("批量删除成功", null);
    }

    private AdminQuestionDTO convertToDTO(Question question) {
        AdminQuestionDTO dto = new AdminQuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setCategoryId(question.getCategoryId());
        dto.setExplanation(question.getExplanation());
        dto.setCreateTime(question.getCreateTime() != null ? question.getCreateTime().format(formatter) : null);

        // 获取分类名称
        if (question.getCategoryId() != null) {
            Category category = categoryMapper.selectById(question.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }

        // 获取选项
        QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
        optionWrapper.eq("question_id", question.getId());
        optionWrapper.orderByAsc("option_id");
        List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);

        List<AdminQuestionDTO.OptionDTO> optionDTOs = options.stream().map(opt -> {
            AdminQuestionDTO.OptionDTO optDTO = new AdminQuestionDTO.OptionDTO();
            optDTO.setOptionId(opt.getOptionId());
            optDTO.setContent(opt.getContent());
            return optDTO;
        }).collect(Collectors.toList());

        dto.setOptions(optionDTOs);

        return dto;
    }

    private void updateCategoryQuestionCount(Long categoryId) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId);
        Long count = questionMapper.selectCount(wrapper);

        Category category = categoryMapper.selectById(categoryId);
        if (category != null) {
            category.setQuestionCount(count.intValue());
            categoryMapper.updateById(category);
        }
    }
}