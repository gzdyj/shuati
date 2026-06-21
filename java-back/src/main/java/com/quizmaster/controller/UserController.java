package com.quizmaster.controller;

import com.quizmaster.common.Result;
import com.quizmaster.dto.LoginDTO;
import com.quizmaster.dto.RegisterDTO;
import com.quizmaster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        try {
            Map<String, Object> data = userService.login(dto);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success("注册成功", null);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        try {
            Map<String, Object> data = userService.getProfile(userId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
