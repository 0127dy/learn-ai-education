package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员反馈/评价实体
 */
@Data
@TableName("feedback")
public class Feedback {
    /** 主键ID */
    private Integer id;
    /** 学员ID，关联 student 表 */
    private Integer studentId;
    /** 反馈内容 */
    private String content;
    /** 情感得分（-1.0 ~ 1.0，由AI分析得出，负值=消极，正值=积极） */
    private Double sentimentScore;
    /** 创建时间 */
    private LocalDateTime createTime;
}
