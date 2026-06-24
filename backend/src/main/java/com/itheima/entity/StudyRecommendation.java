package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习推荐实体（由AI分析生成）
 */
@Data
@TableName("study_recommendation")
public class StudyRecommendation {
    /** 主键ID */
    private Integer id;
    /** 学员ID，关联 student 表 */
    private Integer studentId;
    /** 推荐内容 */
    private String content;
    /** 推荐理由 */
    private String reason;
    /**
     * 推荐类型
     * course  — 课程推荐
     * method  — 学习方法推荐
     * material — 学习资料推荐
     */
    private String type;
    /** 创建时间 */
    private LocalDateTime createTime;
}
