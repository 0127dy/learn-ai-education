package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分析报告实体（由AI生成的各类分析报告）
 */
@Data
@TableName("analysis_report")
public class AnalysisReport {
    /** 主键ID */
    private Integer id;
    /** 报告标题 */
    private String title;
    /**
     * 报告类型
     * performance — 成绩预测报告
     * cluster     — 学员聚类分析报告
     * sentiment   — 情感分析报告
     */
    private String type;
    /** 报告摘要 */
    private String summary;
    /** 详细数据（JSON格式，包含图表数据等） */
    private String detailJson;
    /** 创建时间 */
    private LocalDateTime createTime;
}
