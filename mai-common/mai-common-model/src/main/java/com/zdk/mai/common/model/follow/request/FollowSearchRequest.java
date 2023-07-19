package com.zdk.mai.common.model.follow.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/20 17:18
 */
@Data
public class FollowSearchRequest extends BaseSearchRequest {

    private Long userId;

    /** 1是获取用户关注的  2是获取用户的粉丝 */
    private Integer type;

}
