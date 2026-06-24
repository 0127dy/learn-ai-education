package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.PageResult;
import com.itheima.dto.StudentCountReportData;
import com.itheima.dto.StudentDegreeReportData;
import com.itheima.entity.Student;
import com.itheima.entity.Clazz;
import com.itheima.mapper.StudentMapper;
import com.itheima.mapper.ClazzMapper;
import com.itheima.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StudentMapper studentMapper;
    private final ClazzMapper clazzMapper;

    @Override
    public List<StudentDegreeReportData> studentDegreeData() {
        List<Student> allStudents = studentMapper.selectList(null);

        Map<Integer, String> degreeMap = new LinkedHashMap<>();
        degreeMap.put(1, "初中");
        degreeMap.put(2, "高中");
        degreeMap.put(3, "大专");
        degreeMap.put(4, "本科");
        degreeMap.put(5, "硕士");
        degreeMap.put(6, "博士");

        Map<Integer, Long> countMap = allStudents.stream()
                .filter(s -> s.getDegree() != null)
                .collect(Collectors.groupingBy(Student::getDegree, Collectors.counting()));

        List<StudentDegreeReportData> data = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : degreeMap.entrySet()) {
            Long count = countMap.getOrDefault(entry.getKey(), 0L);
            if (count > 0) {
                data.add(new StudentDegreeReportData(entry.getValue(), count.intValue()));
            }
        }
        return data;
    }

    @Override
    public StudentCountReportData studentCountData() {
        List<Student> allStudents = studentMapper.selectList(null);
        List<Clazz> allClazzs = clazzMapper.selectList(null);

        Map<Integer, String> clazzNameMap = allClazzs.stream()
                .collect(Collectors.toMap(Clazz::getId, Clazz::getName, (a, b) -> a));

        Map<Integer, Long> countMap = allStudents.stream()
                .filter(s -> s.getClazzId() != null)
                .collect(Collectors.groupingBy(Student::getClazzId, Collectors.counting()));

        List<String> clazzList = new ArrayList<>();
        List<Integer> dataList = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : countMap.entrySet()) {
            String clazzName = clazzNameMap.getOrDefault(entry.getKey(), "班级-" + entry.getKey());
            clazzList.add(clazzName);
            dataList.add(entry.getValue().intValue());
        }

        return new StudentCountReportData(clazzList, dataList);
    }
}
