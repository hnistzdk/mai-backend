package com.zdk.mai.file.controller;

import cn.hutool.core.lang.UUID;
import com.upyun.Result;
import com.zdk.mai.common.core.constant.CommonConstants;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.file.vo.FileVO;
import com.zdk.mai.common.security.annotation.InnerAuth;
import com.zdk.mai.common.security.annotation.RateLimiter;
import com.zdk.mai.file.service.IFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/5 19:53
 */
@Slf4j
@Validated
@RestController
@Api(tags = "文件服务接口")
public class FileController {

    @Autowired
    private IFileService fileService;


    @ApiOperation(value = "上传文件", notes = "上传文件")
    @InnerAuth
    @PostMapping(value = "/file/upload")
    public CommonResult upload(@RequestBody byte[] fileBytes, @RequestParam(value = "filename") String filename, @RequestParam(value = "base") String base) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Result result = fileService.upload(fileBytes, filename, base);
        return CommonResult.getResult(result.isSucceed());
    }

    @ApiOperation(value = "上传图片", notes = "上传图片")
    @RateLimiter(prefix = "file",key = "uploadImg",expireTime = 1,maxCount = 1,rateMessage = "操作过于频繁")
    @PostMapping(value = "/file/uploadImg")
    public CommonResult<FileVO> uploadImg(@RequestParam(name = "image") MultipartFile multipartFile, @RequestParam(value = "base") String base) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String fileType = "."+StringUtils.substringAfterLast(multipartFile.getOriginalFilename(), ".");
        String filename = UUID.fastUUID() + fileType;
        fileService.upload(multipartFile.getBytes(), filename, base);
        FileVO vo = new FileVO();
        vo.setUid(UUID.fastUUID().toString());
        vo.setFilename(filename);
        vo.setUrl(CommonConstants.DOMAIN_PREFIX + base+filename);
        return CommonResult.success(vo);
    }

}
