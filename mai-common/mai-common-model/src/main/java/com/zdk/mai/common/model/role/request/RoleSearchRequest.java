package com.zdk.mai.common.model.role.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/5 17:17
 */
@Data
public class RoleSearchRequest extends BaseSearchRequest {

    private String roleName;

}
