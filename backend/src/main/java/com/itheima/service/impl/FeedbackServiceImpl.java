package com.itheima.service.impl;

import com.itheima.entity.Feedback;
import com.itheima.mapper.FeedbackMapper;
import com.itheima.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 反馈 Service 实现
 */
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Override
    public void add(Feedback feedback) {
        feedback.setCreateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }

    @Override
    public List<Feedback> listByStudentId(Integer studentId) {
        return feedbackMapper.findByStudentId(studentId);
    }
}
