package com.itheima.service;

import com.itheima.entity.AnalysisReport;

import java.util.List;

/**
 * 分析报告 Service 接口
 */
public interface AnalysisReportService {

    /**
     * 保存分析报告
     */
    void save(AnalysisReport report);

    /**
     * 查询最近的分析报告
     */
    List<AnalysisReport> listRecent(String type, int limit);

    /**
     * 查看最新指定类型的报告
     */
    AnalysisReport getLatestByType(String type);
}
