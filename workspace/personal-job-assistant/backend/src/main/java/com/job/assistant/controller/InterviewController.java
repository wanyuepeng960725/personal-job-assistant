package com.job.assistant.controller;

import com.job.assistant.dto.CommonResponse;
import com.job.assistant.dto.CreateInterviewRequest;
import com.job.assistant.dto.UpdateInterviewRequest;
import com.job.assistant.entity.Interview;
import com.job.assistant.service.InterviewService;
import com.job.assistant.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 面试记录控制器
 */
@RestController
@RequestMapping("/interview")
@CrossOrigin(origins = "*")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取面试记录列表（分页）
     */
    @GetMapping("/list")
    public CommonResponse<Map<String, Object>> getInterviewList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String authorization
    ) {
        // 从Token中获取用户ID
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return CommonResponse.error(401, "Token无效");
        }

        return interviewService.getInterviewList(userId, company, status, page, pageSize);
    }

    /**
     * 获取面试记录详情
     */
    @GetMapping("/{id}")
    public CommonResponse<Interview> getInterviewById(@PathVariable Long id) {
        return interviewService.getInterviewById(id);
    }

    /**
     * 创建面试记录
     */
    @PostMapping("/create")
    public CommonResponse<Interview> createInterview(
            @Valid @RequestBody CreateInterviewRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        // 从Token中获取用户ID
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return CommonResponse.error(401, "Token无效");
        }

        return interviewService.createInterview(userId, request);
    }

    /**
     * 更新面试记录
     */
    @PutMapping("/{id}")
    public CommonResponse<Interview> updateInterview(
            @PathVariable Long id,
            @RequestBody UpdateInterviewRequest request
    ) {
        return interviewService.updateInterview(id, request);
    }

    /**
     * 完成面试
     */
    @PostMapping("/{id}/complete")
    public CommonResponse<Interview> completeInterview(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String result = request.get("result");
        String notes = request.get("notes");

        if (result == null) {
            return CommonResponse.error("面试结果不能为空");
        }

        return interviewService.completeInterview(id, result, notes);
    }

    /**
     * 删除面试记录
     */
    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteInterview(@PathVariable Long id) {
        return interviewService.deleteInterview(id);
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return null;
        }

        return jwtUtil.getUserIdFromToken(token);
    }
}
