package com.itheima.service.impl;

import com.itheima.entity.StudentPerformance;
import com.itheima.mapper.StudentPerformanceMapper;
import com.itheima.service.StudentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学员成绩 Service 实现
 */
@Service
@RequiredArgsConstructor
public class StudentPerformanceServiceImpl implements StudentPerformanceService {

    private final StudentPerformanceMapper studentPerformanceMapper;

    @Override
    public void add(StudentPerformance performance) {
        // 自动设置创建时间
        performance.setCreateTime(LocalDateTime.now());
        studentPerformanceMapper.insert(performance);
    }

    @Override
    public List<StudentPerformance> listByStudentId(Integer studentId) {
        return studentPerformanceMapper.findByStudentId(studentId);
    }
}
