package com.job.assistant.service;

import com.job.assistant.dto.CommonResponse;
import com.job.assistant.dto.CreateInterviewRequest;
import com.job.assistant.dto.UpdateInterviewRequest;
import com.job.assistant.entity.Interview;
import com.job.assistant.mapper.InterviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 面试记录服务
 */
@Service
public class InterviewService {

    @Autowired
    private InterviewMapper interviewMapper;

    /**
     * 获取面试记录列表（分页）
     */
    public CommonResponse<Map<String, Object>> getInterviewList(Long userId, String company, String status, Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询列表
        List<Interview> list = interviewMapper.findByUserId(userId, company, status, offset, pageSize);

        // 查询总数
        Integer total = interviewMapper.countByUserId(userId, company, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return CommonResponse.success(result);
    }

    /**
     * 获取面试记录详情
     */
    public CommonResponse<Interview> getInterviewById(Long id) {
        Interview interview = interviewMapper.findById(id);
        if (interview != null) {
            return CommonResponse.success(interview);
        } else {
            return CommonResponse.error("面试记录不存在");
        }
    }

    /**
     * 创建面试记录
     */
    public CommonResponse<Interview> createInterview(Long userId, CreateInterviewRequest request) {
        Interview interview = new Interview();
        interview.setUserId(userId);
        interview.setCompany(request.getCompany());
        interview.setPosition(request.getPosition());

        // 转换日期字符串为LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        interview.setInterviewDate(LocalDateTime.parse(request.getInterviewDate(), formatter));

        interview.setLocation(request.getLocation());
        interview.setStatus("PENDING"); // 默认状态为待面试
        interview.setNotes(request.getNotes());

        int result = interviewMapper.insert(interview);
        if (result > 0) {
            return CommonResponse.success(interview);
        } else {
            return CommonResponse.error("创建失败");
        }
    }

    /**
     * 更新面试记录
     */
    public CommonResponse<Interview> updateInterview(Long id, UpdateInterviewRequest request) {
        // 检查面试记录是否存在
        Interview existingInterview = interviewMapper.findById(id);
        if (existingInterview == null) {
            return CommonResponse.error("面试记录不存在");
        }

        // 更新字段
        if (request.getCompany() != null) {
            existingInterview.setCompany(request.getCompany());
        }
        if (request.getPosition() != null) {
            existingInterview.setPosition(request.getPosition());
        }
        if (request.getInterviewDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            existingInterview.setInterviewDate(LocalDateTime.parse(request.getInterviewDate(), formatter));
        }
        if (request.getLocation() != null) {
            existingInterview.setLocation(request.getLocation());
        }
        if (request.getStatus() != null) {
            existingInterview.setStatus(request.getStatus());
        }
        if (request.getResult() != null) {
            existingInterview.setResult(request.getResult());
        }
        if (request.getNotes() != null) {
            existingInterview.setNotes(request.getNotes());
        }

        int result = interviewMapper.update(existingInterview);
        if (result > 0) {
            return CommonResponse.success(existingInterview);
        } else {
            return CommonResponse.error("更新失败");
        }
    }

    /**
     * 完成面试
     */
    public CommonResponse<Interview> completeInterview(Long id, String result, String notes) {
        // 检查面试记录是否存在
        Interview interview = interviewMapper.findById(id);
        if (interview == null) {
            return CommonResponse.error("面试记录不存在");
        }

        // 更新状态和结果
        interview.setStatus("COMPLETED");
        interview.setResult(result);
        if (notes != null) {
            interview.setNotes(notes);
        }

        int updateResult = interviewMapper.update(interview);
        if (updateResult > 0) {
            return CommonResponse.success(interview);
        } else {
            return CommonResponse.error("操作失败");
        }
    }

    /**
     * 删除面试记录
     */
    public CommonResponse<Void> deleteInterview(Long id) {
        // 检查面试记录是否存在
        Interview interview = interviewMapper.findById(id);
        if (interview == null) {
            return CommonResponse.error("面试记录不存在");
        }

        int result = interviewMapper.delete(id);
        if (result > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error("删除失败");
        }
    }
}
