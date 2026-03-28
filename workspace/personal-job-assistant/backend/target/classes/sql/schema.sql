-- 个人求职助手数据库表结构

-- 创建数据库
CREATE DATABASE IF NOT EXISTS job_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE job_assistant;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 面试记录表
CREATE TABLE IF NOT EXISTS `interview` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '面试记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `company` VARCHAR(100) NOT NULL COMMENT '公司名称',
  `position` VARCHAR(100) NOT NULL COMMENT '职位',
  `interview_date` TIMESTAMP NOT NULL COMMENT '面试时间',
  `location` VARCHAR(255) NOT NULL COMMENT '面试地点',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待面试, COMPLETED-已完成, PASSED-通过, FAILED-未通过, CANCELLED-已取消',
  `result` VARCHAR(50) DEFAULT NULL COMMENT '面试结果',
  `notes` TEXT DEFAULT NULL COMMENT '备注',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_interview_date` (`interview_date`),
  CONSTRAINT `fk_interview_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试记录表';

-- 插入测试数据
INSERT INTO `user` (`username`, `email`, `password`) VALUES
('testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH');

INSERT INTO `interview` (`user_id`, `company`, `position`, `interview_date`, `location`, `status`, `result`, `notes`) VALUES
(1, '字节跳动', '前端工程师', '2026-03-20 14:00:00', '北京市海淀区中关村软件园', 'PENDING', NULL, NULL),
(1, '阿里巴巴', 'Java开发工程师', '2026-03-18 10:00:00', '杭州市余杭区', 'COMPLETED', '通过', '面试表现良好，技术能力符合要求'),
(1, '腾讯', '产品经理', '2026-03-25 15:00:00', '深圳市南山区', 'PENDING', NULL, NULL);
