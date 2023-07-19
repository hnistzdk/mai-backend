package com.zdk.mai.common.model.dynamic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.core.request.BaseCreateRequest;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 11:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_user_dynamic")
public class UserDynamicModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "dynamic_id",type = IdType.ASSIGN_ID)
    private Long dynamicId;


    private Long userId;

    /**
     * 类型（1发帖、2评论、3点赞、4关注等）
     */
    private Integer type;

    /**
     * 操作对象id
     */
    private Long objectId;

    /**
     * 操作对象id
     */
    private Long commentId;

    /** 动态状态(1不启用，0启用) */
    private Boolean state;

    public UserDynamicModel build(BaseCreateRequest baseCreateRequest){
        this.setCreateBy(baseCreateRequest.getCreateBy());
        this.setCreateTime(baseCreateRequest.getCreateTime());
        this.setUpdateBy(baseCreateRequest.getUpdateBy());
        this.setUpdateTime(baseCreateRequest.getUpdateTime());
        return this;
    }

}
