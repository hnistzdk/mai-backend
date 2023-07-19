package com.zdk.mai.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zdk.mai.common.model.comment.CommentModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 19:34
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentModel> {
}
