package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.entity.StudyRecommendation;
import com.itheima.mapper.StudyRecommendationMapper;
import com.itheima.service.StudyRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习推荐 Service 实现
 */
@Service
@RequiredArgsConstructor
public class StudyRecommendationServiceImpl implements StudyRecommendationService {

    private final StudyRecommendationMapper recommendationMapper;

    @Override
    public void save(StudyRecommendation recommendation) {
        recommendation.setCreateTime(LocalDateTime.now());
        recommendationMapper.insert(recommendation);
    }

    @Override
    public List<StudyRecommendation> listByStudentId(Integer studentId) {
        return recommendationMapper.findByStudentId(studentId);
    }

    @Override
    public void deleteByStudentId(Integer studentId) {
        LambdaQueryWrapper<StudyRecommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyRecommendation::getStudentId, studentId);
        recommendationMapper.delete(wrapper);
    }
}
