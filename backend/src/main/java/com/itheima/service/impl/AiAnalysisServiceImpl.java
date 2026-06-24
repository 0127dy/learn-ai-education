package com.itheima.service.impl;

import cn.hutool.json.JSONUtil;
import com.itheima.entity.StudentPerformance;
import com.itheima.service.AiAnalysisService;
import com.itheima.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 分析服务实现
 * 通过 RestTemplate 调用 Python AI 服务（端口 5001）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final RestTemplate restTemplate;
    private final StudentPerformanceService performanceService;

    /**
     * AI 服务地址，从配置文件读取
     */
    @Value("${ai-service.base-url:http://localhost:5001}")
    private String aiBaseUrl;

    @Override
    public Map<String, Object> predictPerformance(Integer studentId) {
        // 1. 获取该学员的历史成绩
        List<StudentPerformance> performances = performanceService.listByStudentId(studentId);
        if (performances.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 0);
            error.put("message", "该学员暂无成绩记录，无法预测");
            return error;
        }

        // 2. 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("student_id", studentId);
        // 将成绩数据转为 [{courseName, score, examDate}, ...]
        List<Map<String, Object>> scores = performances.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("course", p.getCourseName());
            m.put("score", p.getScore());
            m.put("exam_date", p.getExamDate().toString());
            return m;
        }).toList();
        requestBody.put("scores", scores);

        // 3. 调用 AI 服务
        return callAiService("/api/predict/performance", requestBody);
    }

    @Override
    public Map<String, Object> clusterAnalysis(Integer clazzId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("clazz_id", clazzId);
        return callAiService("/api/analyze/cluster", requestBody);
    }

    @Override
    public Map<String, Object> analyzeSentiment(List<String> feedbackContents) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("texts", feedbackContents);
        return callAiService("/api/analyze/sentiment", requestBody);
    }

    @Override
    public Map<String, Object> getRecommendations(Integer studentId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("student_id", studentId);
        // 带上学员的历史成绩作为参考
        List<StudentPerformance> performances = performanceService.listByStudentId(studentId);
        if (!performances.isEmpty()) {
            List<Map<String, Object>> scores = performances.stream().map(p -> {
                Map<String, Object> m = new HashMap<>();
                m.put("course", p.getCourseName());
                m.put("score", p.getScore());
                return m;
            }).toList();
            requestBody.put("scores", scores);
        }
        return callAiService("/api/recommend/courses", requestBody);
    }

    /**
     * 调用外部 Python AI 服务的通用方法
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> callAiService(String path, Map<String, Object> requestBody) {
        String url = aiBaseUrl + path;
        log.info("调用 AI 服务: {}，请求参数: {}", url, JSONUtil.toJsonStr(requestBody));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            log.info("AI 服务响应: {}", response);
            return response != null ? response : Map.of("code", 0, "message", "AI 服务无响应");
        } catch (Exception e) {
            log.error("调用 AI 服务失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", -1);
            error.put("message", "AI 服务调用失败: " + e.getMessage());
            return error;
        }
    }
}
