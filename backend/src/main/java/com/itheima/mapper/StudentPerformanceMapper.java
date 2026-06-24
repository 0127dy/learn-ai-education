package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.StudentPerformance;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学员成绩 Mapper
 */
public interface StudentPerformanceMapper extends BaseMapper<StudentPerformance> {

    /**
     * 查询某学员的所有成绩，按考试日期升序排列
     */
    @Select("SELECT * FROM student_performance WHERE student_id = #{studentId} ORDER BY exam_date ASC")
    List<StudentPerformance> findByStudentId(Integer studentId);
}
