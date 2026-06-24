package com.itheima.service;

import com.itheima.entity.StudentPerformance;

import java.util.List;

/**
 * 学员成绩 Service 接口
 */
public interface StudentPerformanceService {

    /**
     * 添加成绩
     */
    void add(StudentPerformance performance);

    /**
     * 查看某学员的所有成绩
     */
    List<StudentPerformance> listByStudentId(Integer studentId);
}
