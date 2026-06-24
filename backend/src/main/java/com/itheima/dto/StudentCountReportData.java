package com.itheima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 班级人数统计 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCountReportData {
    private List<String> clazzList; // 班级名称列表
    private List<Integer> dataList; // 对应班级人数列表
}
