package com.zdk.mai.file.service;

import com.upyun.Result;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/5 19:53
 */
public interface IFileService {

    Result upload(byte[] fileBytes, String filename, String base) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;

}
