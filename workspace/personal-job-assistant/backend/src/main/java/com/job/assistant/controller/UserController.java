package com.job.assistant.controller;

import com.job.assistant.dto.CommonResponse;
import com.job.assistant.entity.User;
import com.job.assistant.service.UserService;
import com.job.assistant.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public CommonResponse<User> getUserInfo(@RequestHeader("Authorization") String authorization) {
        // 提取Token
        String token = extractToken(authorization);
        if (token == null) {
            return CommonResponse.error(401, "未提供Token");
        }

        return userService.getUserByToken(token);
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public CommonResponse<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 从Authorization header中提取Token
     */
    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }
}
