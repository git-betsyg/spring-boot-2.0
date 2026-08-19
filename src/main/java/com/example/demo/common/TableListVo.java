package com.example.demo.common;

import lombok.Data;

/**
 * 统一返回列表接口类型
 */
@Data
public class TableListVo<T> {

    /**
     * 列表
     */
    private T[] list;

    /**
     * 当前页
     */
    private int current;

    /**
     * 每页显示的数据条数
     */
    private int pageSize;

    /**
     * 总数量
     */
    private int total;

    /**
     * 返回列表数据
     *
     * @param list     列表
     * @param current  当前页
     * @param pageSize 每页显示的数据条数
     * @param total    总数量
     */
    public TableListVo(T[] list, int current, int pageSize, int total) {
        this.list = list;
        this.current = current;
        this.pageSize = pageSize;
        this.total = total;
    }
}
