-- 学生管理系统初始化SQL
-- 如果数据库不存在则创建
CREATE DATABASE IF NOT EXISTS student DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student;

-- 班级表
CREATE TABLE IF NOT EXISTS clazz (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(50) NOT NULL COMMENT '班级名称',
    room        VARCHAR(10) COMMENT '教室',
    begin_date  DATE COMMENT '开班日期',
    end_date    DATE COMMENT '结课日期',
    master_id   INT COMMENT '班主任ID',
    subject     TINYINT COMMENT '学科(1:Java,2:前端,3:大数据,4:Python,5:其他)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT '班级表';

-- 学员表
CREATE TABLE IF NOT EXISTS student (
    id               INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name             VARCHAR(32) NOT NULL COMMENT '姓名',
    no               VARCHAR(32) UNIQUE NOT NULL COMMENT '学号',
    gender           TINYINT DEFAULT 1 COMMENT '性别(1:男,2:女)',
    phone            VARCHAR(11) COMMENT '手机号',
    id_card          VARCHAR(18) COMMENT '身份证号',
    is_college       TINYINT DEFAULT 0 COMMENT '是否来自于院校(1:是,0:否)',
    address          VARCHAR(100) COMMENT '地址',
    degree           TINYINT COMMENT '学历(1:初中,2:高中,3:大专,4:本科,5:硕士,6:博士)',
    graduation_date  DATE COMMENT '毕业日期',
    clazz_id         INT COMMENT '班级ID',
    violation_count  INT DEFAULT 0 COMMENT '违纪次数',
    violation_score  INT DEFAULT 0 COMMENT '违纪扣分',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) COMMENT '学员表';

-- 初始数据
INSERT INTO clazz (id, name, room, begin_date, end_date, master_id, subject) VALUES
(1, 'JavaEE就业100期', '101', '2023-04-01', '2023-12-30', 1, 1),
(2, 'JavaEE就业101期', '102', '2023-05-01', '2024-01-30', 2, 1);

INSERT INTO student (id, name, no, gender, phone, id_card, is_college, address, degree, graduation_date, clazz_id) VALUES
(1, '张三', '2023001001', 1, '13800138001', '110101199001011234', 1, '北京市海淀区', 4, '2020-07-01', 1),
(2, '李四', '2023001002', 1, '13800138002', '110101199001021234', 1, '北京市朝阳区', 3, '2020-07-01', 1),
(3, 'Lily', '2023001003', 2, '13309230912', '110090110090110090', 0, '回龙观东大街110号', 4, '2020-07-01', 1),
(4, '王五', '2023001004', 1, '13800138003', '110101199101011234', 0, '上海市浦东新区', 4, '2021-06-30', 1),
(5, '赵六', '2023001005', 1, '13800138004', '110101199201011234', 1, '广州市天河区', 4, '2021-07-01', 2),
(6, '孙七', '2023001006', 2, '13800138005', '110101199301011234', 0, '深圳市南山区', 3, '2020-07-01', 2);

-- ================================================================= --
-- AI 学情分析相关表                                                                       --
-- ================================================================= --

-- 学员成绩表
CREATE TABLE IF NOT EXISTS student_performance (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id  INT NOT NULL COMMENT '学员ID',
    course_name VARCHAR(50) NOT NULL COMMENT '课程名称',
    score       DOUBLE NOT NULL COMMENT '分数(0-100)',
    exam_date   DATETIME COMMENT '考试日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_student (student_id)
) COMMENT '学员成绩表';

-- 学员反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id               INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id       INT NOT NULL COMMENT '学员ID',
    content          TEXT NOT NULL COMMENT '反馈内容',
    sentiment_score  DOUBLE DEFAULT NULL COMMENT '情感得分(-1~1)',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_feedback_student (student_id)
) COMMENT '学员反馈表';

-- 学习推荐表
CREATE TABLE IF NOT EXISTS study_recommendation (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id  INT NOT NULL COMMENT '学员ID',
    content     TEXT NOT NULL COMMENT '推荐内容',
    reason      VARCHAR(500) COMMENT '推荐理由',
    type        VARCHAR(20) NOT NULL DEFAULT 'course' COMMENT '推荐类型(course/method/material)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_rec_student (student_id)
) COMMENT '学习推荐表';

-- 分析报告表
CREATE TABLE IF NOT EXISTS analysis_report (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title       VARCHAR(200) NOT NULL COMMENT '报告标题',
    type        VARCHAR(50) COMMENT '报告类型(performance/cluster/sentiment)',
    summary     TEXT COMMENT '摘要',
    detail_json TEXT COMMENT '详细数据(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_report_type (type)
) COMMENT '分析报告表';

-- ========== 演示数据 ==========

-- 学员成绩演示数据
INSERT INTO student_performance (student_id, course_name, score, exam_date) VALUES
(1, 'Java基础', 85.0, '2023-05-10 09:00:00'),
(1, 'MySQL',    78.0, '2023-06-15 09:00:00'),
(1, 'JavaWeb',   72.0, '2023-07-20 09:00:00'),
(1, 'SSM框架',   80.0, '2023-08-25 09:00:00'),
(1, 'Spring Boot', 88.0, '2023-09-30 09:00:00'),
(2, 'Java基础', 90.0, '2023-05-10 09:00:00'),
(2, 'MySQL',    85.0, '2023-06-15 09:00:00'),
(2, 'JavaWeb',   82.0, '2023-07-20 09:00:00'),
(2, 'SSM框架',   88.0, '2023-08-25 09:00:00'),
(2, 'Spring Boot', 92.0, '2023-09-30 09:00:00'),
(3, 'Java基础', 95.0, '2023-05-10 09:00:00'),
(3, 'MySQL',    92.0, '2023-06-15 09:00:00'),
(3, 'JavaWeb',   88.0, '2023-07-20 09:00:00'),
(3, 'SSM框架',   91.0, '2023-08-25 09:00:00'),
(3, 'Spring Boot', 94.0, '2023-09-30 09:00:00'),
(4, 'Java基础', 65.0, '2023-05-10 09:00:00'),
(4, 'MySQL',    70.0, '2023-06-15 09:00:00'),
(4, 'JavaWeb',   60.0, '2023-07-20 09:00:00'),
(4, 'SSM框架',   68.0, '2023-08-25 09:00:00'),
(4, 'Spring Boot', 72.0, '2023-09-30 09:00:00');

-- 学员反馈演示数据
INSERT INTO feedback (student_id, content, sentiment_score) VALUES
(1, '课程内容很充实，老师讲解清晰，学到了很多实用的技术', 0.8),
(2, '项目实战环节很好，建议增加更多企业级案例', 0.5),
(3, '学习氛围很好，同学之间互相帮助，老师也很耐心', 0.9),
(4, '有些内容难度较大，希望能有更多基础辅导', 0.1);

-- 分析报告演示数据
INSERT INTO analysis_report (title, type, summary, detail_json) VALUES
('班级1学员成绩预测报告', 'performance', '基于学员历史成绩的期末成绩预测', '{"predictions":[{"student":"张三","predicted_score":82.5},{"student":"李四","predicted_score":87.3},{"student":"Lily","predicted_score":92.1}]}'),
('班级1学员聚类分析报告', 'cluster', 'KMeans聚类分析将学员分为3类', '{"clusters":[{"label":"优秀组","count":2,"members":["李四","Lily"]},{"label":"中等组","count":1,"members":["张三"]},{"label":"提升组","count":1,"members":["王五"]}]}');
