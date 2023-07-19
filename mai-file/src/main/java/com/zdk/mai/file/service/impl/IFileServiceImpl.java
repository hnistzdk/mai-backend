package com.zdk.mai.file.service.impl;

import com.UpYun;
import com.upyun.FormUploader;
import com.upyun.Result;
import com.zdk.mai.file.service.IFileService;
import com.zdk.starter.properties.UpYunProperties;
import com.zdk.starter.service.UpYunOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/5 19:55
 */
@Service
public class IFileServiceImpl implements IFileService {

    @Resource
    private UpYunOssService upYunOssService;

    @Override
    public Result upload(byte[] fileBytes, String filename, String base) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return upYunOssService.uploadFile(filename, base, fileBytes);
    }
}
