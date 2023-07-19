package com.zdk.mai.common.model.message.vo;

import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 17:42
 */
@Data
public class MessageCountVO {

    private Integer replyCount;

    private Integer likeCount;

    private Integer followCount;

    private Integer systemCount;

    private Integer allCount;


}
