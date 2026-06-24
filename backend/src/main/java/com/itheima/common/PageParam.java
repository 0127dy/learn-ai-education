package com.itheima.common;

import lombok.Data;

/**
 * 统一分页查询参数
 */
@Data
public class PageParam {
    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getPage() {
        return page == null || page < 1 ? 1 : page;
    }

    public Integer getPageSize() {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}
