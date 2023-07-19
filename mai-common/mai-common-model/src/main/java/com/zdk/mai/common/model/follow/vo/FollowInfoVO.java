package com.zdk.mai.common.model.follow.vo;

import com.zdk.mai.common.core.response.BaseVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/20 17:20
 */
@Data
public class FollowInfoVO extends BaseVO {

    private Long toUser;

    private String toUsername;

    private Long fromUser;

    private String fromUsername;

    private UserInfoVO userInfo;

}
