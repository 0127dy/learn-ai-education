package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.entity.StudentPerformance;
import com.itheima.service.AiAnalysisService;
import com.itheima.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学员成绩管理 Controller
 * /api/performance/**
 */
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final StudentPerformanceService performanceService;
    private final AiAnalysisService aiAnalysisService;

    /**
     * 添加成绩
     * POST /api/performance
     */
    @PostMapping
    public Result add(@RequestBody StudentPerformance performance) {
        performanceService.add(performance);
        return Result.success();
    }

    /**
     * 查询某学员的所有成绩
     * GET /api/performance/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public Result listByStudentId(@PathVariable Integer studentId) {
        return Result.success(performanceService.listByStudentId(studentId));
    }

    /**
     * 调用 AI 预测学员期末成绩
     * GET /api/performance/predict?studentId=1
     */
    @GetMapping("/predict")
    public Result predict(@RequestParam Integer studentId) {
        Map<String, Object> result = aiAnalysisService.predictPerformance(studentId);
        return Result.success(result);
    }
}
