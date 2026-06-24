package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.entity.StudyRecommendation;
import com.itheima.service.AiAnalysisService;
import com.itheima.service.StudyRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 个性化学习推荐 Controller
 * /api/recommend/**
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final AiAnalysisService aiAnalysisService;
    private final StudyRecommendationService recommendationService;

    /**
     * 获取个性化学习推荐（调用 AI）
     * GET /api/recommend/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public Result getRecommendations(@PathVariable Integer studentId) {
        // 1. 先查数据库是否有已生成的推荐
        List<StudyRecommendation> existing = recommendationService.listByStudentId(studentId);
        if (!existing.isEmpty()) {
            return Result.success(existing);
        }

        // 2. 没有缓存，调用 AI 服务生成推荐
        Map<String, Object> aiResult = aiAnalysisService.getRecommendations(studentId);

        // 3. 如果AI服务返回成功，保存推荐到数据库
        if (aiResult.containsKey("recommendations")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recommendations = (List<Map<String, Object>>) aiResult.get("recommendations");
            for (Map<String, Object> rec : recommendations) {
                StudyRecommendation sr = new StudyRecommendation();
                sr.setStudentId(studentId);
                sr.setContent((String) rec.getOrDefault("content", ""));
                sr.setReason((String) rec.getOrDefault("reason", ""));
                sr.setType((String) rec.getOrDefault("type", "course"));
                recommendationService.save(sr);
            }
        }

        // 4. 返回 AI 结果
        return Result.success(aiResult);
    }
}
