package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Student {
    private Integer id;
    private String name;
    private String no;
    private Integer gender;
    private String phone;
    private String idCard;
    private Integer isCollege;
    private String address;
    private Integer degree;
    private LocalDate graduationDate;
    private Integer clazzId;
    @TableField(exist = false)
    private String clazzName;
    private Integer violationCount;
    private Integer violationScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
