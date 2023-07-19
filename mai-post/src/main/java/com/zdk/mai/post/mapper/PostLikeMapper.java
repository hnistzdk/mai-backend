package com.zdk.mai.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zdk.mai.common.model.post.PostLikeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 16:47
 */
@Mapper
public interface PostLikeMapper extends BaseMapper<PostLikeModel> {

    /**
     * 获取用户贴子+职言获赞总数
     * @param userId
     * @return
     */
    Integer getUserPostLikeCount(@Param("userId") Long userId);

}
