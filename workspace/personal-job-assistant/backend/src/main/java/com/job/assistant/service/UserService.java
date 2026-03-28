package com.job.assistant.service;

import com.job.assistant.dto.CommonResponse;
import com.job.assistant.dto.LoginRequest;
import com.job.assistant.dto.LoginResponse;
import com.job.assistant.dto.RegisterRequest;
import com.job.assistant.entity.User;
import com.job.assistant.mapper.UserMapper;
import com.job.assistant.util.JwtUtil;
import com.job.assistant.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public CommonResponse<LoginResponse> register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            return CommonResponse.error("用户名已存在");
        }

        // 检查邮箱是否已存在
        existingUser = userMapper.findByEmail(request.getEmail());
        if (existingUser != null) {
            return CommonResponse.error("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.encrypt(request.getPassword()));

        int result = userMapper.insert(user);
        if (result > 0) {
            // 生成Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            LoginResponse response = new LoginResponse(token, user);
            return CommonResponse.success(response);
        } else {
            return CommonResponse.error("注册失败");
        }
    }

    /**
     * 用户登录
     */
    public CommonResponse<LoginResponse> login(LoginRequest request) {
        // 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            return CommonResponse.error("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.verify(request.getPassword(), user.getPassword())) {
            return CommonResponse.error("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        LoginResponse response = new LoginResponse(token, user);
        return CommonResponse.success(response);
    }

    /**
     * 根据ID获取用户信息
     */
    public CommonResponse<User> getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            return CommonResponse.success(user);
        } else {
            return CommonResponse.error("用户不存在");
        }
    }

    /**
     * 根据Token获取用户信息
     */
    public CommonResponse<User> getUserByToken(String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return CommonResponse.error(401, "Token无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return CommonResponse.error("无法从Token中获取用户信息");
        }

        return getUserById(userId);
    }
}
