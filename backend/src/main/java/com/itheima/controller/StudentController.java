package com.itheima.controller;

import com.itheima.common.PageResult;
import com.itheima.common.Result;
import com.itheima.common.StudentQueryParam;
import com.itheima.entity.Student;
import com.itheima.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学员管理 Controller
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 学员列表条件分页查询
     * GET /students?name=张三&degree=1&clazzId=2&page=1&pageSize=5
     */
    @GetMapping
    public Result list(StudentQueryParam param) {
        PageResult pageResult = studentService.list(param);
        return Result.success(pageResult);
    }

    /**
     * 批量删除学员
     * DELETE /students/{ids}
     */
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable String ids) {
        studentService.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 添加学员
     * POST /students
     */
    @PostMapping
    public Result add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success();
    }

    /**
     * 根据ID查询学员
     * GET /students/{id}
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    /**
     * 修改学员
     * PUT /students
     */
    @PutMapping
    public Result update(@RequestBody Student student) {
        studentService.update(student);
        return Result.success();
    }

    /**
     * 违纪处理
     * PUT /students/violation/{id}/{score}
     */
    @PutMapping("/violation/{id}/{score}")
    public Result handleViolation(@PathVariable Integer id, @PathVariable Integer score) {
        studentService.handleViolation(id, score);
        return Result.success();
    }
}
