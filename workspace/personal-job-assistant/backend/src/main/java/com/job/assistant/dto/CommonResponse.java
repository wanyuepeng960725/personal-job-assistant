package com.job.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(200, "操作成功", null);
    }

    /**
     * 失败响应
     */
    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(500, message, null);
    }

    /**
     * 失败响应（自定义错误码）
     */
    public static <T> CommonResponse<T> error(Integer code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
