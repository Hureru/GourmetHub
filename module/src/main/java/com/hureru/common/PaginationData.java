package com.hureru.common;

import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 * 用于封装分页查询的结果数据和分页信息
 * @author zheng
 */
@Data
public class PaginationData<T> {
    /**
     * 分页信息对象
     * 包含总记录数、总页数、当前页码、每页记录数等分页相关参数
     */
    private Pagination pagination;

    /**
     * 当前页的数据列表
     * 存储分页查询返回的具体业务数据
     */
    private List<T> data;

    public PaginationData(List<T> content, long totalElements, int totalPages, int number, int size) {
        this.setData(content);
        this.setPagination(new Pagination(totalElements,totalPages, size, number));
    }

    /**
     * 分页信息内部类
     * 封装分页相关的元数据信息
     */
    @Data
    public static class Pagination {
        /**
         * 总记录数
         * 表示符合查询条件的记录总数
         */
        private long totalItems;

        /**
         * 总页数
         * 根据总记录数和每页记录数计算得出的总页数
         */
        private int totalPages;

        /**
         * 当前页码
         * 表示当前显示的是第几页，从1开始计数
         */
        private int currentPage;

        /**
         * 每页记录数
         * 表示每页显示多少条记录
         */
        private int pageSize;

        public Pagination(long totalElements,int totalPages, int size, int number) {
            this.setTotalItems(totalElements);
            this.setTotalPages(totalPages);
            this.setPageSize(size);
            this.setCurrentPage(number + 1);
        }
    }

}

