package com.zdk.mai.common.model.user.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/4 21:36
 */
@Data
public class UserSearchRequest extends BaseSearchRequest {

    private Long userId;

}
