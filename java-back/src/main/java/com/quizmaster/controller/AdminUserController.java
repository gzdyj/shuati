package com.quizmaster.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quizmaster.common.Result;
import com.quizmaster.dto.AdminUserDTO;
import com.quizmaster.entity.AnswerRecord;
import com.quizmaster.entity.User;
import com.quizmaster.mapper.AnswerRecordMapper;
import com.quizmaster.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public Result<Page<AdminUserDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("username", keyword);
        }

        userMapper.selectPage(userPage, wrapper);

        List<AdminUserDTO> dtoList = userPage.getRecords().stream().map(user -> {
            AdminUserDTO dto = new AdminUserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole() != null ? user.getRole() : "USER");
            dto.setCreateTime(user.getCreateTime() != null ? user.getCreateTime().format(formatter) : null);

            // 计算答题统计
            QueryWrapper<AnswerRecord> answerWrapper = new QueryWrapper<>();
            answerWrapper.eq("user_id", user.getId());
            Long totalAnswered = answerRecordMapper.selectCount(answerWrapper);
            dto.setTotalAnswered(totalAnswered.intValue());

            answerWrapper.eq("is_correct", 1);
            Long correctCount = answerRecordMapper.selectCount(answerWrapper);
            dto.setCorrectCount(correctCount.intValue());

            if (totalAnswered > 0) {
                dto.setCorrectRate((double) correctCount / totalAnswered * 100);
            } else {
                dto.setCorrectRate(0.0);
            }

            return dto;
        }).collect(Collectors.toList());

        Page<AdminUserDTO> resultPage = new Page<>(page, size);
        resultPage.setRecords(dtoList);
        resultPage.setTotal(userPage.getTotal());

        return Result.success(resultPage);
    }

    @GetMapping("/{id}")
    public Result<AdminUserDTO> detail(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole() != null ? user.getRole() : "USER");
        dto.setCreateTime(user.getCreateTime() != null ? user.getCreateTime().format(formatter) : null);

        QueryWrapper<AnswerRecord> answerWrapper = new QueryWrapper<>();
        answerWrapper.eq("user_id", user.getId());
        Long totalAnswered = answerRecordMapper.selectCount(answerWrapper);
        dto.setTotalAnswered(totalAnswered.intValue());

        answerWrapper.eq("is_correct", 1);
        Long correctCount = answerRecordMapper.selectCount(answerWrapper);
        dto.setCorrectCount(correctCount.intValue());

        if (totalAnswered > 0) {
            dto.setCorrectRate((double) correctCount / totalAnswered * 100);
        } else {
            dto.setCorrectRate(0.0);
        }

        return Result.success(dto);
    }

    @PutMapping("/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody User userUpdate) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (userUpdate.getRole() != null) {
            user.setRole(userUpdate.getRole());
            userMapper.updateById(user);
        }

        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 删除用户的答题记录
        QueryWrapper<AnswerRecord> answerWrapper = new QueryWrapper<>();
        answerWrapper.eq("user_id", id);
        answerRecordMapper.delete(answerWrapper);

        // 删除用户
        userMapper.deleteById(id);

        return Result.success("删除成功", null);
    }
}