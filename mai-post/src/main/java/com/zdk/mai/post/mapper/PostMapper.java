package com.zdk.mai.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zdk.mai.common.model.post.PostModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/28 17:38
 */
@Mapper
public interface PostMapper extends BaseMapper<PostModel> {

    /**
     * 获取用户贴子的总pv
     * @param userId
     * @return
     */
    Long getUserPostPv(@Param("userId") Long userId);


    /**
     * 用户点赞的贴子
     * @param userId
     * @return
     */
    List<PostModel> likeList(@Param("userId")Long userId);

}
