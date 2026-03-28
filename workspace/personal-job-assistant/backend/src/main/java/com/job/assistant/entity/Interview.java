package com.job.assistant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 面试记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interview {

    /**
     * 面试记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

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
    private LocalDateTime interviewDate;

    /**
     * 面试地点
     */
    private String location;

    /**
     * 状态：PENDING-待面试, COMPLETED-已完成, PASSED-通过, FAILED-未通过, CANCELLED-已取消
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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
