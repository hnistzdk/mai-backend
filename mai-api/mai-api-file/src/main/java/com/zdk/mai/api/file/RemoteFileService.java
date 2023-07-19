package com.zdk.mai.api.file;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.constant.ServiceNameConstants;
import com.zdk.mai.common.core.response.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description 文件服务暴露接口
 * @Author zdk
 * @Date 2023/3/5 20:11
 */
@FeignClient(value = ServiceNameConstants.FILE_SERVICE)
public interface RemoteFileService {

    /**
     * 上传文件
     * @param fileBytes 文件
     * @param filename 文件名
     * @param base base路径
     * @param source 内部请求标志
     * @return
     */
    @PostMapping(value = "/file/upload")
    CommonResult upload(@RequestBody byte[] fileBytes, @RequestParam(value = "filename") String filename, @RequestParam(value = "base") String base, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
