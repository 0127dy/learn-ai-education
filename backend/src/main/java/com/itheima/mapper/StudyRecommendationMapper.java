package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.StudyRecommendation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学习推荐 Mapper
 */
public interface StudyRecommendationMapper extends BaseMapper<StudyRecommendation> {

    /**
     * 查询某学员的所有推荐
     */
    @Select("SELECT * FROM study_recommendation WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<StudyRecommendation> findByStudentId(Integer studentId);
}
