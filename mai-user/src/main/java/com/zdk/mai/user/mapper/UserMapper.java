package com.zdk.mai.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.zdk.mai.common.model.user.UserModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 16:38
 */
@Mapper
public interface UserMapper extends BaseMapper<UserModel> {
}
