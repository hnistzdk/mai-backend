package com.zdk.mai.common.model.follow.vo;

import com.zdk.mai.common.core.response.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/20 18:06
 */
@Data
@AllArgsConstructor
public class FollowCountVO extends BaseVO {

    /** 关注数*/
    private Long followCount;

    /** 粉丝数*/
    private Long fanCount;

}
