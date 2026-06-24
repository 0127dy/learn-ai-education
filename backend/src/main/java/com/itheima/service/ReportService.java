package com.itheima.service;

import com.itheima.dto.StudentCountReportData;
import com.itheima.dto.StudentDegreeReportData;
import java.util.List;

public interface ReportService {
    List<StudentDegreeReportData> studentDegreeData();
    StudentCountReportData studentCountData();
}
