package com.job.assistant.mapper;

import com.job.assistant.entity.Interview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试记录Mapper接口
 */
@Mapper
public interface InterviewMapper {

    /**
     * 根据ID查询面试记录
     */
    Interview findById(@Param("id") Long id);

    /**
     * 查询用户的所有面试记录（分页）
     */
    List<Interview> findByUserId(
            @Param("userId") Long userId,
            @Param("company") String company,
            @Param("status") String status,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 统计用户面试记录数量
     */
    Integer countByUserId(
            @Param("userId") Long userId,
            @Param("company") String company,
            @Param("status") String status
    );

    /**
     * 插入面试记录
     */
    int insert(Interview interview);

    /**
     * 更新面试记录
     */
    int update(Interview interview);

    /**
     * 删除面试记录
     */
    int delete(@Param("id") Long id);

    /**
     * 根据用户ID删除所有面试记录
     */
    int deleteByUserId(@Param("userId") Long userId);
}
