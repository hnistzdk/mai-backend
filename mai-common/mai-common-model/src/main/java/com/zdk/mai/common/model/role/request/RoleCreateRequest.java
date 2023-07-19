package com.zdk.mai.common.model.role.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/5 18:24
 */
@Data
public class RoleCreateRequest extends BaseCreateRequest {

    private Long roleId;

    @NotBlank
    private String roleName;

    private Integer roleSort;

}
