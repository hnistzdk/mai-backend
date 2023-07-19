package com.zdk.mai.common.core.response;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 19:23
 */
public class PageResponse<T>{
    private static final long serialVersionUID = 1L;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPage;
    private List<?> list;

    public static <T> PageResponse<T> pageResponse(List<?> data, PageInfo<T> pageInfo){
        PageResponse<T> response = new PageResponse<>();
        response.setTotalCount((int) pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
        response.list = data;
        return response;
    }

    public static <T> PageResponse<T> pageResponse(List<? extends BaseVO> data){
        PageResponse<T> response = new PageResponse<>();
        response.list = data;
        return response;
    }


    public void setTotalCount(Integer totalCount, Integer currPage, Integer pageSize) {
        this.totalCount = totalCount;
        this.currentPage = currPage;
        this.pageSize = pageSize;
        this.totalPage = (totalCount + pageSize - 1) / pageSize;
    }

    public void setTotalCount(Integer totalCount, Integer length) {
        this.totalCount = totalCount;
        this.totalPage = (totalCount + length - 1) / length;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<?> getList() {
        return this.list;
    }

    public void setList(List<? extends BaseVO> list) {
        this.list = list;
    }

    public void setData(List<? extends BaseVO> data, PageInfo<T> pageInfo) {
        this.setTotalCount((int) pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
        this.list = data;
    }

    public Integer getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
