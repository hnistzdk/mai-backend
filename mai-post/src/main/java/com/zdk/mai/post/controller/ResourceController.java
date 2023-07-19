package com.zdk.mai.post.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.resource.ResourceNavigateModel;
import com.zdk.mai.common.model.resource.request.ResourceNavigateCreateRequest;
import com.zdk.mai.common.model.resource.request.ResourceNavigateSearchRequest;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.common.security.annotation.RateLimiter;
import com.zdk.mai.post.service.IResourceNavigateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 22:31
 */
@Slf4j
@RestController
@Validated
@Api(tags = "资源导航接口")
public class ResourceController {

    @Autowired
    private IResourceNavigateService resourceNavigateService;

    @ApiOperation(value = "新增资源导航")
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/resource/create")
    public CommonResult<Boolean> create(@RequestBody ResourceNavigateCreateRequest createRequest) {

        resourceNavigateService.create(createRequest);

        return CommonResult.success();
    }


    @ApiOperation(value = "上传资源导航logo")
    @RateLimiter(prefix = "resource",key = "uploadResourceLogo",limitIp = false, expireTime = 60*60*24,maxCount = 1,rateMessage = "每人每天只能新增一个资源")
    @PostMapping("/resource/uploadResourceLogo")
    public CommonResult<String> uploadResourceLogo(@RequestParam(value = "logo", required = false) MultipartFile logo) throws IOException {
        if (isFileNotTooBig(logo.getBytes())) {
            return CommonResult.success(resourceNavigateService.uploadResourceNavigateLogo(logo.getBytes(), logo.getOriginalFilename()));
        } else {
            throw new BusinessException("上传图片过大");
        }
    }

    @ApiOperation(value = "更新资源导航")
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/resource/update")
    public CommonResult update(@RequestBody ResourceNavigateCreateRequest createRequest) {

        resourceNavigateService.update(createRequest);

        return CommonResult.success();
    }

    @ApiOperation(value = "获取资源导航")
    @GetMapping("/resource/list")
    @NotRequiresLogin
    public CommonResult<PageInfo<ResourceNavigateModel>> list(ResourceNavigateSearchRequest searchRequest) {
        return CommonResult.success(resourceNavigateService.getList(searchRequest));
    }

    @ApiOperation(value = "获取资源导航所有类别")
    @GetMapping("/resource/categories")
    @NotRequiresLogin
    public CommonResult<List<String>> categories() {
        return CommonResult.success(resourceNavigateService.getCategories());
    }

    @PostMapping("/resource/delete/{id}")
    @ApiOperation(value = "删除资源导航")
    public CommonResult<Boolean> delete(@PathVariable("id") Integer id) {

        resourceNavigateService.delete(id);

        return CommonResult.success();
    }

    /**
     * 上传源文件允许的最大值不得大于fileLength
     */
    public static final long FILE_MAX_LENGTH = 5242880;

    /**
     * 文件是否过大
     *
     * @param bytes
     * @return
     */
    private Boolean isFileNotTooBig(byte[] bytes) {
        // 当前文件大小
        long currentFileSize = bytes.length;
        return currentFileSize <= FILE_MAX_LENGTH;
    }

}
