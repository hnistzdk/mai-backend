package com.zdk.mai.api.file.fallback;

import com.zdk.mai.api.file.RemoteFileService;
import com.zdk.mai.common.core.response.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 10:59
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);
    @Override
    public RemoteFileService create(Throwable cause) {

        log.error("文件服务调用失败:{}", cause.getMessage());

        return new RemoteFileService() {
            @Override
            public CommonResult upload(byte[] fileBytes, String filename, String base, String source) {
                return CommonResult.fail("上传文件失败:"+cause.getMessage());
            }
        };
    }
}
