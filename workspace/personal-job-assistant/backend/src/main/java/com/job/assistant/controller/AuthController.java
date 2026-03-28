package com.job.assistant.controller;

import com.job.assistant.dto.CommonResponse;
import com.job.assistant.dto.LoginRequest;
import com.job.assistant.dto.LoginResponse;
import com.job.assistant.dto.RegisterRequest;
import com.job.assistant.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public CommonResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
