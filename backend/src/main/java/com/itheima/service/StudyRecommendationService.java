package com.itheima.service;

import com.itheima.entity.StudyRecommendation;

import java.util.List;

/**
 * 学习推荐 Service 接口
 */
public interface StudyRecommendationService {

    /**
     * 保存推荐结果
     */
    void save(StudyRecommendation recommendation);

    /**
     * 查看某学员的所有推荐
     */
    List<StudyRecommendation> listByStudentId(Integer studentId);

    /**
     * 删除某学员的所有旧推荐
     */
    void deleteByStudentId(Integer studentId);
}
