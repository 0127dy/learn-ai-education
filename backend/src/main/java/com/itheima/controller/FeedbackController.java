package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.entity.Feedback;
import com.itheima.mapper.FeedbackMapper;
import com.itheima.service.AiAnalysisService;
import com.itheima.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学员反馈管理 Controller
 * /api/feedback/**
 */
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AiAnalysisService aiAnalysisService;

    /**
     * 提交反馈
     * POST /api/feedback
     */
    @PostMapping
    public Result add(@RequestBody Feedback feedback) {
        feedbackService.add(feedback);
        return Result.success();
    }

    /**
     * 查询某学员的所有反馈
     * GET /api/feedback/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public Result listByStudentId(@PathVariable Integer studentId) {
        return Result.success(feedbackService.listByStudentId(studentId));
    }

    /**
     * 分析班级反馈情感趋势（调用 AI）
     * GET /api/feedback/sentiment/class/{clazzId}
     */
    @GetMapping("/sentiment/class/{clazzId}")
    public Result sentimentAnalysis(@PathVariable Integer clazzId) {
        // 这里简化为获取该班级学生的反馈内容
        // 实际项目中可通过班级ID查询该班级下所有学生的反馈
        // 为了演示，我们构造一个返回
        Map<String, Object> result = new HashMap<>();
        result.put("clazz_id", clazzId);
        result.put("message", "情感分析功能需要AI服务配合，请确保ai-service已启动");
        return Result.success(result);
    }
}
