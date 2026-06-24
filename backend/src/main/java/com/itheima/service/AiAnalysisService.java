package com.itheima.service;

import java.util.List;
import java.util.Map;

/**
 * AI 分析服务接口
 * 负责调用外部 Python AI 服务进行各类智能分析
 */
public interface AiAnalysisService {

    /**
     * 预测学员成绩
     * @param studentId 学员ID
     * @return 预测结果（包含预测分数、置信度等）
     */
    Map<String, Object> predictPerformance(Integer studentId);

    /**
     * 学员聚类分析
     * @param clazzId 班级ID
     * @return 聚类分析结果
     */
    Map<String, Object> clusterAnalysis(Integer clazzId);

    /**
     * 情感分析
     * @param feedbackContents 反馈内容列表
     * @return 情感分析结果（每条反馈的情感得分）
     */
    Map<String, Object> analyzeSentiment(List<String> feedbackContents);

    /**
     * 获取个性化学习推荐
     * @param studentId 学员ID
     * @return 推荐结果
     */
    Map<String, Object> getRecommendations(Integer studentId);
}
