package com.hureru.common;

import lombok.Data;

/**
 * @author zheng
 */
@Data
public class PaginationData {
    private Pagination pagination;
    private Object[] data;

    @Data
    public static class Pagination {
        private int totalItems;
        private int totalPages;
        private int currentPage;
        private int pageSize;
    }

}
