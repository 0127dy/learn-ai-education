package com.itheima.service;

import com.itheima.common.PageResult;
import com.itheima.common.StudentQueryParam;
import com.itheima.entity.Student;

public interface StudentService {
    PageResult list(StudentQueryParam param);
    void deleteByIds(String ids);
    void add(Student student);
    Student getById(Integer id);
    void update(Student student);
    void handleViolation(Integer id, Integer score);
}
