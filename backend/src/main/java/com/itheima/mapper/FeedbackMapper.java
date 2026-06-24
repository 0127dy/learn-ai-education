package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Feedback;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学员反馈 Mapper
 */
public interface FeedbackMapper extends BaseMapper<Feedback> {

    /**
     * 查询某学员的所有反馈
     */
    @Select("SELECT * FROM feedback WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<Feedback> findByStudentId(Integer studentId);
}
