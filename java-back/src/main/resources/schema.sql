-- QuizMaster 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS quiz_master DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE quiz_master;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建默认管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `user` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ADMIN');

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(50) COMMENT '图标',
    `question_count` INT DEFAULT 0 COMMENT '题目数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

-- 题目表
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `content` TEXT NOT NULL COMMENT '题目内容',
    `correct_answer` VARCHAR(10) NOT NULL COMMENT '正确答案',
    `category_id` BIGINT COMMENT '分类ID',
    `explanation` TEXT COMMENT '答案解析',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 题目选项表
CREATE TABLE IF NOT EXISTS `question_option` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `option_id` VARCHAR(10) NOT NULL COMMENT '选项ID(A/B/C/D)',
    `content` VARCHAR(255) NOT NULL COMMENT '选项内容',
    INDEX `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- 答题记录表
CREATE TABLE IF NOT EXISTS `answer_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `user_answer` VARCHAR(10) NOT NULL COMMENT '用户答案',
    `correct_answer` VARCHAR(10) NOT NULL COMMENT '正确答案',
    `is_correct` TINYINT(1) DEFAULT 0 COMMENT '是否正确',
    `answer_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user` (`user_id`),
    INDEX `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- 初始化分类数据
INSERT INTO `category` (`name`, `icon`, `question_count`) VALUES
('政治', '🏛️', 120),
('历史', '📜', 85),
('地理', '🌍', 76),
('法律', '⚖️', 95),
('经济', '💰', 110),
('科技', '🔬', 88);

-- 初始化题目数据
INSERT INTO `question` (`content`, `correct_answer`, `category_id`, `explanation`) VALUES
('中国的首都是哪里？', 'A', 1, '北京是中华人民共和国的首都。'),
('JavaScript是一种什么类型的语言？', 'B', 6, 'JavaScript是一种解释型脚本语言。'),
('以下哪个是Vue3的 Composition API？', 'C', 6, 'setup()是Vue3 Composition API的入口。'),
('中国共产党成立于哪一年？', 'A', 2, '中国共产党成立于1921年。'),
('世界上最长的河流是？', 'B', 3, '尼罗河全长约6650公里，是世界上最长的河流。'),
('民事诉讼法属于什么法律？', 'C', 4, '民事诉讼法属于程序法。'),
('GDP是指什么？', 'A', 5, 'GDP是国内生产总值的英文缩写。'),
('太阳系中最大的行星是？', 'C', 6, '木星是太阳系中最大的行星。');

-- 初始化题目选项
INSERT INTO `question_option` (`question_id`, `option_id`, `content`) VALUES
(1, 'A', '北京'),
(1, 'B', '上海'),
(1, 'C', '广州'),
(1, 'D', '深圳'),
(2, 'A', '编译型语言'),
(2, 'B', '解释型语言'),
(2, 'C', '汇编语言'),
(2, 'D', '机器语言'),
(3, 'A', 'data'),
(3, 'B', 'methods'),
(3, 'C', 'setup'),
(3, 'D', 'computed'),
(4, 'A', '1921年'),
(4, 'B', '1922年'),
(4, 'C', '1920年'),
(4, 'D', '1919年'),
(5, 'A', '亚马逊河'),
(5, 'B', '尼罗河'),
(5, 'C', '长江'),
(5, 'D', '密西西比河'),
(6, 'A', '实体法'),
(6, 'B', '行政法'),
(6, 'C', '程序法'),
(6, 'D', '刑法'),
(7, 'A', '国内生产总值'),
(7, 'B', '国民生产总值'),
(7, 'C', '人均GDP'),
(7, 'D', '消费者物价指数'),
(8, 'A', '土星'),
(8, 'B', '地球'),
(8, 'C', '木星'),
(8, 'D', '海王星');
