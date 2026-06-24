package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.PageResult;
import com.itheima.common.StudentQueryParam;
import com.itheima.entity.Student;
import com.itheima.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StudentService 单元测试 —— Mock Mapper 层，只测 Service 业务逻辑
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Captor
    private ArgumentCaptor<Student> studentCaptor;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1);
        student.setName("张三");
        student.setNo("2024001");
        student.setGender(1);
        student.setDegree(1);
        student.setClazzId(1);
    }

    @Test
    @DisplayName("分页查询学员列表")
    void testList() {
        // 准备
        StudentQueryParam param = new StudentQueryParam();
        param.setPage(1);
        param.setPageSize(10);
        param.setName("张");
        param.setDegree(1);
        param.setClazzId(1);

        Page<Student> mockPage = new Page<>(1, 10, 2);
        mockPage.setRecords(List.of(student));

        when(studentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        // 执行
        PageResult result = studentService.list(param);

        // 验证
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("张三", ((Student) result.getRecords().get(0)).getName());
        verify(studentMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("添加学员成功")
    void testAdd() {
        studentService.add(student);
        verify(studentMapper).insert(studentCaptor.capture());
        assertEquals("张三", studentCaptor.getValue().getName());
    }

    @Test
    @DisplayName("根据ID查询学员")
    void testGetById() {
        when(studentMapper.selectById(1)).thenReturn(student);

        Student result = studentService.getById(1);

        assertNotNull(result);
        assertEquals("张三", result.getName());
        verify(studentMapper).selectById(1);
    }

    @Test
    @DisplayName("ID不存在时返回null")
    void testGetByIdNotFound() {
        when(studentMapper.selectById(999)).thenReturn(null);

        Student result = studentService.getById(999);

        assertNull(result);
    }

    @Test
    @DisplayName("更新学员信息")
    void testUpdate() {
        student.setName("李四");
        studentService.update(student);

        verify(studentMapper).updateById(studentCaptor.capture());
        assertEquals("李四", studentCaptor.getValue().getName());
    }

    @Test
    @DisplayName("批量删除学员")
    void testDeleteByIds() {
        studentService.deleteByIds("1,2,3");

        verify(studentMapper).deleteBatchIds(List.of(1, 2, 3));
    }

    @Test
    @DisplayName("违纪处理 —— 首次违纪")
    void testHandleViolationFirstTime() {
        Student s = new Student();
        s.setId(1);
        s.setName("张三");
        s.setViolationCount(null);
        s.setViolationScore(null);

        when(studentMapper.selectById(1)).thenReturn(s);

        studentService.handleViolation(1, 2);

        verify(studentMapper).updateById(studentCaptor.capture());
        Student updated = studentCaptor.getValue();
        assertEquals(1, updated.getViolationCount());
        assertEquals(2, updated.getViolationScore());
    }

    @Test
    @DisplayName("违纪处理 —— 多次违纪累计")
    void testHandleViolationMultipleTimes() {
        Student s = new Student();
        s.setId(1);
        s.setViolationCount(2);
        s.setViolationScore(5);

        when(studentMapper.selectById(1)).thenReturn(s);

        studentService.handleViolation(1, 3);

        verify(studentMapper).updateById(studentCaptor.capture());
        Student updated = studentCaptor.getValue();
        assertEquals(3, updated.getViolationCount());  // 2 + 1
        assertEquals(8, updated.getViolationScore());   // 5 + 3
    }
}
