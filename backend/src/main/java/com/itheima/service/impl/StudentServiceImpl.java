package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.PageResult;
import com.itheima.common.StudentQueryParam;
import com.itheima.entity.Student;
import com.itheima.mapper.StudentMapper;
import com.itheima.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    @Override
    public PageResult list(StudentQueryParam param) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<Student>()
                .like(StringUtils.isNotBlank(param.getName()), Student::getName, param.getName())
                .eq(param.getDegree() != null, Student::getDegree, param.getDegree())
                .eq(param.getClazzId() != null, Student::getClazzId, param.getClazzId())
                .orderByDesc(Student::getUpdateTime);

        Page<Student> page = studentMapper.selectPage(
                new Page<>(param.getPage(), param.getPageSize()),
                wrapper
        );

        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        studentMapper.deleteBatchIds(idList);
    }

    @Override
    public void add(Student student) {
        studentMapper.insert(student);
    }

    @Override
    public Student getById(Integer id) {
        return studentMapper.selectById(id);
    }

    @Override
    public void update(Student student) {
        studentMapper.updateById(student);
    }

    @Override
    public void handleViolation(Integer id, Integer score) {
        Student student = studentMapper.selectById(id);
        if (student != null) {
            student.setViolationCount((student.getViolationCount() == null ? 0 : student.getViolationCount()) + 1);
            student.setViolationScore((student.getViolationScore() == null ? 0 : student.getViolationScore()) + score);
            studentMapper.updateById(student);
        }
    }
}
