package com.zdk.mai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.role.RoleModel;
import com.zdk.mai.common.model.role.request.RoleCreateRequest;
import com.zdk.mai.common.model.role.request.RoleSearchRequest;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/5 16:31
 */
public interface IRoleService extends IService<RoleModel> {


    /**
     * 角色列表
     * @param searchRequest
     * @return
     */
    PageInfo<RoleModel> page(RoleSearchRequest searchRequest);


    /**
     * 新增或更新角色基本信息
     * @param createRequest
     */
    void create(RoleCreateRequest createRequest);


    /**
     * 删除角色
     * @param roleId
     */
    void delete(Long roleId);
}
