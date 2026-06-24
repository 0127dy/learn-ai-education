package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.entity.AnalysisReport;
import com.itheima.mapper.AnalysisReportMapper;
import com.itheima.service.AnalysisReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分析报告 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AnalysisReportServiceImpl implements AnalysisReportService {

    private final AnalysisReportMapper analysisReportMapper;

    @Override
    public void save(AnalysisReport report) {
        report.setCreateTime(LocalDateTime.now());
        analysisReportMapper.insert(report);
    }

    @Override
    public List<AnalysisReport> listRecent(String type, int limit) {
        LambdaQueryWrapper<AnalysisReport> wrapper = Wrappers.lambdaQuery();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(AnalysisReport::getType, type);
        }
        wrapper.orderByDesc(AnalysisReport::getCreateTime);
        wrapper.last("LIMIT " + limit);
        return analysisReportMapper.selectList(wrapper);
    }

    @Override
    public AnalysisReport getLatestByType(String type) {
        LambdaQueryWrapper<AnalysisReport> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AnalysisReport::getType, type);
        wrapper.orderByDesc(AnalysisReport::getCreateTime);
        wrapper.last("LIMIT 1");
        List<AnalysisReport> list = analysisReportMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }
}
