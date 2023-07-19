package com.zdk.mai.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zdk.mai.common.model.follow.UserFollowModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 12:01
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollowModel> {
}
