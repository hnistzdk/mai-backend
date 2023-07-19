package com.zdk.mai.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.resource.ResourceNavigateModel;
import com.zdk.mai.common.model.resource.request.ResourceNavigateCreateRequest;
import com.zdk.mai.common.model.resource.request.ResourceNavigateSearchRequest;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 22:32
 */
public interface IResourceNavigateService extends IService<ResourceNavigateModel> {

    /**
     * 获取资源导航
     *
     * @param searchRequest
     * @return
     */
    PageInfo<ResourceNavigateModel> getList(ResourceNavigateSearchRequest searchRequest);

    /**
     * 新增资源导航
     *
     * @param navigateCreateRequest
     * @return
     */
    void create(ResourceNavigateCreateRequest navigateCreateRequest);

    /**
     * 上传资源导航logo
     *
     * @param bytes
     * @param sourceFileName
     * @return
     */
    String uploadResourceNavigateLogo(byte[] bytes, String sourceFileName);

    /**
     * 更新资源导航
     *
     * @param navigateCreateRequest
     * @return
     */
    void update(ResourceNavigateCreateRequest navigateCreateRequest);

    /**
     * 删除资源导航
     *
     * @param id
     * @return
     */
    void delete(Integer id);

    /**
     * 获取资源导航所有类别
     *
     * @return
     */
    List<String> getCategories();

}
