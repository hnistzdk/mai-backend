package com.zdk.mai.user.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.constant.CommonMessage;
import com.zdk.mai.common.core.constant.CommonStatus;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.model.role.RoleModel;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.request.UserSearchRequest;
import com.zdk.mai.common.model.user.request.UserUpdateRequest;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.security.annotation.RequiresRoles;
import com.zdk.mai.user.service.IRoleService;
import com.zdk.mai.user.service.IUserService;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/4 21:34
 */
@Api(tags = "管理端用户相关接口")
@Validated
@RestController
@RequestMapping("/admin/user")
@RequiresRoles({"管理员"})
public class AdminUserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;


    @ApiOperation(value = "管理端用户列表", notes = "管理端用户列表")
    @GetMapping("/list")
    public CommonResult list(UserSearchRequest searchRequest){

        PageInfo<UserModel> pageInfo = userService.list(searchRequest);

        List<UserInfoVO> voList = getVOList(pageInfo);

        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }

    /**
     * 获取vo
     * @param pageInfo
     * @return
     */
    private List<UserInfoVO> getVOList(PageInfo<UserModel> pageInfo){
        List<UserModel> modelList = pageInfo.getList();

        List<UserInfoVO> voList = new ArrayList<>(modelList.size());

        for (UserModel model : modelList) {

            UserInfoVO vo = new UserInfoVO();

            BeanUtils.copyProperties(model, vo);

            RoleModel role = roleService.getById(vo.getRoleId());

            if (role != null){
                vo.setRoleName(role.getRoleName());
            }

            voList.add(vo);
        }

        return voList;

    }


    @ApiOperation(value = "管理端更新用户信息", notes = "管理端更新用户信息")
    @PostMapping("/update")
    public CommonResult update(@RequestBody @Valid UserUpdateRequest updateRequest){

        userService.update(updateRequest);

        return CommonResult.success();
    }




}
