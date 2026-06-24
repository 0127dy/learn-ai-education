package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Clazz {
    private Integer id;
    private String name;
    private String room;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Integer masterId;
    @TableField(exist = false)
    private String masterName;
    private Integer subject;
    @TableField(exist = false)
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
