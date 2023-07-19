package com.zdk.mai.common.model.user.vo;

import lombok.Data;

/**
 * @Description 用户个人成就VO
 * @Author zdk
 * @Date 2023/3/19 21:07
 */
@Data
public class UserAchievementVO {

    /** 贴子获赞总数*/
    private Long postLikeCount;

    /** 贴子被阅读总数*/
    private Long postReadCount;

    /** 评论被点赞总数*/
    private Long commentLikeCount;

}
