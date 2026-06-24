package com.itheima.controller;

import com.itheima.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页/健康检查 Controller
 * 用于验证服务是否成功部署
 */
@RestController
public class IndexController {

    @Value("${server.port:8080}")
    private String port;

    @GetMapping("/")
    public Result index() {
        return Result.success("Hello K8S！学生管理系统已启动，当前端口：" + port);
    }

    @GetMapping("/health")
    public Result health() {
        return Result.success("UP");
    }
}
