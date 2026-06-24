package com.itheima.service;

import com.itheima.common.ClazzQueryParam;
import com.itheima.common.PageResult;
import com.itheima.entity.Clazz;
import java.util.List;

public interface ClazzService {
    PageResult list(ClazzQueryParam param);
    void delete(Integer id);
    void add(Clazz clazz);
    Clazz getById(Integer id);
    void update(Clazz clazz);
    List<Clazz> listAll();
}
