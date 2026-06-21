package com.quizmaster.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quizmaster.common.Result;
import com.quizmaster.dto.CategoryDTO;
import com.quizmaster.entity.Category;
import com.quizmaster.mapper.CategoryMapper;
import com.quizmaster.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @GetMapping
    public Result<List<CategoryDTO>> list() {
        List<Category> categories = categoryMapper.selectList(null);

        List<CategoryDTO> dtoList = categories.stream().map(category -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setIcon(category.getIcon());

            // 实时计算题目数量
            QueryWrapper<com.quizmaster.entity.Question> wrapper = new QueryWrapper<>();
            wrapper.eq("category_id", category.getId());
            Long count = questionMapper.selectCount(wrapper);
            dto.setQuestionCount(count.intValue());

            return dto;
        }).collect(Collectors.toList());

        return Result.success(dtoList);
    }

    @PostMapping
    public Result<Void> create(@RequestBody CategoryDTO dto) {
        // 检查分类名称是否已存在
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", dto.getName());
        if (categoryMapper.selectCount(wrapper) > 0) {
            return Result.error(400, "分类名称已存在");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setQuestionCount(0);

        categoryMapper.insert(category);

        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        // 检查新名称是否与其他分类重复
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", dto.getName());
        wrapper.ne("id", id);
        if (categoryMapper.selectCount(wrapper) > 0) {
            return Result.error(400, "分类名称已存在");
        }

        category.setName(dto.getName());
        category.setIcon(dto.getIcon());

        categoryMapper.updateById(category);

        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        // 检查分类下是否有题目
        QueryWrapper<com.quizmaster.entity.Question> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", id);
        if (questionMapper.selectCount(wrapper) > 0) {
            return Result.error(400, "该分类下还有题目，无法删除");
        }

        categoryMapper.deleteById(id);

        return Result.success("删除成功", null);
    }
}