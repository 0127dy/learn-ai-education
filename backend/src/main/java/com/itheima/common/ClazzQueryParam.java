package com.itheima.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级列表查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClazzQueryParam extends PageParam {
    private String name;
    private String begin;
    private String end;
}
