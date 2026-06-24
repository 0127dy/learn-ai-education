package com.itheima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学历分布统计 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDegreeReportData {
    private String name;  // 学历名称：初中、高中、大专、本科、硕士、博士
    private Integer count; // 对应学历的人数
}
