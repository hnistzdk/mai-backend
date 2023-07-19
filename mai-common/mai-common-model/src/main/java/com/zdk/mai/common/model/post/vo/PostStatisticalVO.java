package com.zdk.mai.common.model.post.vo;

import com.zdk.mai.common.core.response.BaseVO;
import lombok.Data;

/**
 * @Description 贴子统计数据Vo
 * @Author zdk
 * @Date 2023/3/3 21:20
 */
@Data
public class PostStatisticalVO extends BaseVO {

    /** 总评论数 */
    private Long commentCount;

    /** 总点赞数 */
    private Long likeCount;

    /** 该登录用户是否已点赞 */
    private Boolean like;


}
