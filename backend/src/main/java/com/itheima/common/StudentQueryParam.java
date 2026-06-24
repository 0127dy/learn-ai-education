package com.itheima.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学员列表查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQueryParam extends PageParam {
    private String name;
    private Integer degree;
    private Integer clazzId;
}
