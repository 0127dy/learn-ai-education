package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员各科成绩实体
 */
@Data
@TableName("student_performance")
public class StudentPerformance {
    /** 主键ID */
    private Integer id;
    /** 学员ID，关联 student 表 */
    private Integer studentId;
    /** 课程名称，如：Java基础、MySQL、Spring Boot */
    private String courseName;
    /** 分数（0-100） */
    private Double score;
    /** 考试日期 */
    private LocalDateTime examDate;
    /** 创建时间 */
    private LocalDateTime createTime;
}
