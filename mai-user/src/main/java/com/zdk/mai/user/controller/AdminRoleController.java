package com.zdk.mai.user.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.model.role.RoleModel;
import com.zdk.mai.common.model.role.request.RoleCreateRequest;
import com.zdk.mai.common.model.role.request.RoleSearchRequest;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.RequiresRoles;
import com.zdk.mai.user.service.IRoleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/5 16:30
 */
@Api(tags = "管理端角色相关接口")
@Validated
@RestController
@RequestMapping("/admin/role")
@RequiresRoles({"admin"})
public class AdminRoleController {

    @Autowired
    private IRoleService roleService;


    @ApiOperation(value = "角色列表", notes = "角色列表")
    @GetMapping("/list")
    public CommonResult list(RoleSearchRequest searchRequest){


        PageInfo<RoleModel> pageInfo = roleService.page(searchRequest);

        return CommonResult.success(PageResponse.pageResponse(pageInfo.getList(), pageInfo));
    }

    @ApiOperation(value = "角色列表", notes = "角色列表")
    @GetMapping("/selectData")
    public CommonResult selectData(){

        List<RoleModel> list = roleService.lambdaQuery().orderByAsc(RoleModel::getRoleSort).list();

        return CommonResult.success(list);
    }

    @ApiOperation(value = "新增角色", notes = "新增角色")
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/create")
    public CommonResult create(@RequestBody @Valid RoleCreateRequest createRequest){

        roleService.create(createRequest);

        return CommonResult.success();
    }

    @ApiOperation(value = "更新角色基本信息", notes = "更新角色基本信息")
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/update")
    public CommonResult update(@RequestBody @Valid RoleCreateRequest createRequest){

        roleService.create(createRequest);

        return CommonResult.success();
    }

    @ApiOperation(value = "删除角色", notes = "删除角色")
    @PostMapping("/delete/{roleId}")
    public CommonResult delete(@PathVariable("roleId") Long roleId){

        roleService.delete(roleId);

        return CommonResult.success();
    }

}
