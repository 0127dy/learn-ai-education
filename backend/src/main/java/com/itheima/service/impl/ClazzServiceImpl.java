package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.ClazzQueryParam;
import com.itheima.common.PageResult;
import com.itheima.entity.Clazz;
import com.itheima.mapper.ClazzMapper;
import com.itheima.service.ClazzService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClazzServiceImpl implements ClazzService {

    private final ClazzMapper clazzMapper;

    @Override
    public PageResult list(ClazzQueryParam param) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<Clazz>()
                .like(StringUtils.isNotBlank(param.getName()), Clazz::getName, param.getName())
                .ge(StringUtils.isNotBlank(param.getBegin()), Clazz::getBeginDate, param.getBegin())
                .le(StringUtils.isNotBlank(param.getEnd()), Clazz::getEndDate, param.getEnd())
                .orderByDesc(Clazz::getUpdateTime);

        Page<Clazz> page = clazzMapper.selectPage(
                new Page<>(param.getPage(), param.getPageSize()),
                wrapper
        );

        // 填充班级状态
        LocalDate now = LocalDate.now();
        for (Clazz clazz : page.getRecords()) {
            if (clazz.getBeginDate() != null && !now.isBefore(clazz.getBeginDate())) {
                clazz.setStatus("已开班");
            } else {
                clazz.setStatus("未开班");
            }
        }

        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public void delete(Integer id) {
        clazzMapper.deleteById(id);
    }

    @Override
    public void add(Clazz clazz) {
        clazzMapper.insert(clazz);
    }

    @Override
    public Clazz getById(Integer id) {
        return clazzMapper.selectById(id);
    }

    @Override
    public void update(Clazz clazz) {
        clazzMapper.updateById(clazz);
    }

    @Override
    public List<Clazz> listAll() {
        return clazzMapper.selectList(null);
    }
}
