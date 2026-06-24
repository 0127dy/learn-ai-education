package com.itheima.service;

import com.itheima.entity.Feedback;

import java.util.List;

/**
 * 反馈 Service 接口
 */
public interface FeedbackService {

    /**
     * 提交反馈
     */
    void add(Feedback feedback);

    /**
     * 查看某学员的所有反馈
     */
    List<Feedback> listByStudentId(Integer studentId);
}
