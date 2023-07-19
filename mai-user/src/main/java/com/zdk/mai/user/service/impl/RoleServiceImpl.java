package com.zdk.mai.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.role.RoleModel;
import com.zdk.mai.common.model.role.request.RoleCreateRequest;
import com.zdk.mai.common.model.role.request.RoleSearchRequest;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.user.mapper.RoleMapper;
import com.zdk.mai.user.service.IRoleService;
import com.zdk.mai.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/5 16:31
 */
@Slf4j
@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, RoleModel> implements IRoleService {

    @Autowired
    private IUserService userService;

    @Override
    public PageInfo<RoleModel> page(RoleSearchRequest searchRequest) {

        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());

        String roleName = searchRequest.getRoleName();

        List<RoleModel> list = lambdaQuery()
                .like(isOk(roleName), RoleModel::getRoleName, roleName)
                .orderByAsc(RoleModel::getRoleSort)
                .list();

        return new PageInfo<>(list);

    }

    @Override
    public void create(RoleCreateRequest createRequest) {

        String roleName = createRequest.getRoleName();

        RoleModel one = lambdaQuery().select(RoleModel::getRoleId)
                .eq(RoleModel::getRoleName, roleName).one();

        if (one != null && isOk(one.getRoleId())){
            throw new BusinessException(ErrorEnum.UNIQUENESS_ERROR.getCode(), "角色名已存在,请重新输入");
        }

        Long roleId = createRequest.getRoleId();

        RoleModel model = new RoleModel();

        BeanUtils.copyProperties(createRequest, model);

        if (notOk(roleId)){
            baseMapper.insert(model);
        }else {
            baseMapper.updateById(model);
        }
    }

    @Override
    public void delete(Long roleId) {
        baseMapper.deleteById(roleId);

        List<UserModel> list = userService.lambdaQuery()
                .eq(UserModel::getRoleId, roleId).list();

        //将所有此角色用户的角色置为null
        list.forEach(e -> e.setRoleId(null));

        userService.updateBatchById(list);

    }
}
