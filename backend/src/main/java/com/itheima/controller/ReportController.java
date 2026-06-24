package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.dto.StudentCountReportData;
import com.itheima.dto.StudentDegreeReportData;
import com.itheima.entity.AnalysisReport;
import com.itheima.service.AiAnalysisService;
import com.itheima.service.AnalysisReportService;
import com.itheima.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据统计 + AI分析报告 Controller
 * 保留原有 /report/** 接口，新增 /report/ai/** AI 分析报告接口
 */
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final AiAnalysisService aiAnalysisService;
    private final AnalysisReportService analysisReportService;

    // ==================== 原有数据统计接口（不变） ====================

    /**
     * 学员学历统计
     * GET /report/studentDegreeData
     */
    @GetMapping("/studentDegreeData")
    public Result studentDegreeData() {
        List<StudentDegreeReportData> data = reportService.studentDegreeData();
        return Result.success(data);
    }

    /**
     * 班级人数统计
     * GET /report/studentCountData
     */
    @GetMapping("/studentCountData")
    public Result studentCountData() {
        StudentCountReportData data = reportService.studentCountData();
        return Result.success(data);
    }

    // ==================== 新增 AI 分析报告接口 ====================

    /**
     * 成绩预测报告（AI驱动）
     * GET /report/ai/performance-prediction
     */
    @GetMapping("/ai/performance-prediction")
    public Result performancePrediction() {
        // 先查缓存，有则直接返回
        AnalysisReport cached = analysisReportService.getLatestByType("performance");
        if (cached != null) {
            return Result.success(cached);
        }
        // 否则提示启动AI服务
        return Result.success(Map.of(
                "message", "请先通过成绩预测接口生成预测数据",
                "hint", "调用 GET /api/performance/predict?studentId=xxx 获取单个学员预测"
        ));
    }

    /**
     * 学员聚类分析报告（AI驱动）
     * GET /report/ai/cluster-analysis?clazzId=1
     */
    @GetMapping("/ai/cluster-analysis")
    public Result clusterAnalysis(@RequestParam(required = false, defaultValue = "1") Integer clazzId) {
        Map<String, Object> result = aiAnalysisService.clusterAnalysis(clazzId);
        // 保存报告到数据库
        AnalysisReport report = new AnalysisReport();
        report.setTitle("班级" + clazzId + "学员聚类分析报告");
        report.setType("cluster");
        report.setSummary("对班级" + clazzId + "学员进行KMeans聚类分析");
        report.setDetailJson(result.toString());
        analysisReportService.save(report);
        return Result.success(result);
    }

    /**
     * 班级洞察报告（AI驱动）
     * GET /report/ai/class-insight?clazzId=1
     */
    @GetMapping("/ai/class-insight")
    public Result classInsight(@RequestParam(required = false, defaultValue = "1") Integer clazzId) {
        // 结合聚类分析和成绩预测，生成班级综合洞察
        Map<String, Object> clusterResult = aiAnalysisService.clusterAnalysis(clazzId);
        AnalysisReport report = new AnalysisReport();
        report.setTitle("班级" + clazzId + "综合洞察报告");
        report.setType("performance");
        report.setSummary("综合成绩预测与聚类分析的班级洞察");
        report.setDetailJson(clusterResult.toString());
        analysisReportService.save(report);
        return Result.success(Map.of(
                "clazz_id", clazzId,
                "insight", "班级综合洞察报告已生成",
                "cluster_data", clusterResult,
                "report_id", report.getId()
        ));
    }
}
