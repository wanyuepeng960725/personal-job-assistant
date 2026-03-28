package com.job.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建面试请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterviewRequest {

    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    private String company;

    /**
     * 职位
     */
    @NotBlank(message = "职位不能为空")
    private String position;

    /**
     * 面试时间
     */
    @NotBlank(message = "面试时间不能为空")
    private String interviewDate;

    /**
     * 面试地点
     */
    @NotBlank(message = "面试地点不能为空")
    private String location;

    /**
     * 备注
     */
    private String notes;
}
