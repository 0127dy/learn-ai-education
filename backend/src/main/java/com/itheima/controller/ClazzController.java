package com.itheima.controller;

import com.itheima.common.ClazzQueryParam;
import com.itheima.common.PageResult;
import com.itheima.common.Result;
import com.itheima.entity.Clazz;
import com.itheima.service.ClazzService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理 Controller
 */
@RestController
@RequestMapping("/clazzs")
@RequiredArgsConstructor
public class ClazzController {

    private final ClazzService clazzService;

    /**
     * 班级列表条件分页查询
     * GET /clazzs?name=java&begin=2023-01-01&end=2023-06-30&page=1&pageSize=5
     */
    @GetMapping
    public Result list(ClazzQueryParam param) {
        PageResult pageResult = clazzService.list(param);
        return Result.success(pageResult);
    }

    /**
     * 删除班级
     * DELETE /clazzs/{id}
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        clazzService.delete(id);
        return Result.success();
    }

    /**
     * 添加班级
     * POST /clazzs
     */
    @PostMapping
    public Result add(@RequestBody Clazz clazz) {
        clazzService.add(clazz);
        return Result.success();
    }

    /**
     * 根据ID查询班级
     * GET /clazzs/{id}
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }

    /**
     * 修改班级
     * PUT /clazzs
     */
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        clazzService.update(clazz);
        return Result.success();
    }

    /**
     * 查询所有班级
     * GET /clazzs/list
     */
    @GetMapping("/list")
    public Result listAll() {
        List<Clazz> list = clazzService.listAll();
        return Result.success(list);
    }
}
