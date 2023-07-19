package com.zdk.mai.common.model.follow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 11:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_user_follow")
public class UserFollowModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "follow_id",type = IdType.ASSIGN_ID)
    private Long followId;

    private Long fromUser;

    private String fromUsername;

    private Long toUser;

    private String toUsername;

    private Boolean state;

}
