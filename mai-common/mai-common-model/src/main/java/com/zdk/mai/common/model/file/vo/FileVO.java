package com.zdk.mai.common.model.file.vo;

import com.zdk.mai.common.core.response.BaseVO;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/10 12:32
 */
@Data
public class FileVO extends BaseVO {

    /**
     * 文件唯一标识
     */
    private String uid;

    private String filename;

    private String url;

}
