package com.job.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新面试请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInterviewRequest {

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位
     */
    private String position;

    /**
     * 面试时间
     */
    private String interviewDate;

    /**
     * 面试地点
     */
    private String location;

    /**
     * 状态
     */
    private String status;

    /**
     * 面试结果
     */
    private String result;

    /**
     * 备注
     */
    private String notes;
}
