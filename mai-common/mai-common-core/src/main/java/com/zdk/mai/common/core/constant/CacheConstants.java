package com.zdk.mai.common.core.constant;

/**
 * @Description 缓存常量信息
 * @Author zdk
 * @Date 2022/11/30 15:56
 */
public class CacheConstants {
    /**
     * 缓存有效期，默认720（分钟）
     */
    public static final long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public static final long REFRESH_TIME = 120;

    /**
     * 密码最大错误次数
     */
    public static final int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public static final long PASSWORD_LOCK_TIME = 10;

    /**
     * 每10次访问更新一次db的pv
     */
    public static final long PV_INCR_MAX_COUNT = 10;

    /**
     * 权限缓存前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 已注册的用户名缓存key前缀
     */
    public static final String USER_EXIST_KEY = "user_exist:";

    /**
     * 用户头像链接缓存key
     */
    public static final String USER_AVATAR_KEY = "user_avatar:";

    /**
     * 评论点赞数量hash key  然后key是评论id  value是点赞的数量
     */
    public static final String COMMENT_LIKE_COUNT_KEY = "comment_like_count";

    /**
     * 评论点赞关系hash key  然后key是评论id:用户id  value是点赞的状态 0未赞 1已赞
     */
    public static final String COMMENT_LIKE_RELATION_KEY = "comment_like_relation";

    /**
     * 贴子点赞数量hash key  然后key是贴子id  value是点赞的数量
     */
    public static final String POST_LIKE_COUNT_KEY = "post_like_count";

    /**
     * 贴子点赞关系hash key  然后key是贴子id:用户id  value是点赞的状态 0未赞 1已赞
     */
    public static final String POST_LIKE_RELATION_KEY = "post_like_relation";

    /**
     * 用户关注数量hash key  然后key是userId  value是关注的数量
     */
    public static final String USER_FOLLOW_COUNT_KEY = "user_follow_count";

    /**
     * 用户关注数量hash key  然后key是userId  value是粉丝的数量
     */
    public static final String USER_FAN_COUNT_KEY = "user_fan_count";

    /**
     * 用户关注关系hash key  然后key是toUser:fromUser  value是关注的状态 0未关注 1已关注
     */
    public static final String USER_FOLLOW_RELATION_KEY = "user_follow_relation";


    /**
     * 邮箱验证码key
     */
    public static final String EMAIL_CODE_KEY = "verify_code:";

    /**
     * 邮箱存在缓存key
     */
    public static final String EMAIL_EXIST_KEY = "email_exist:";


    /**
     * 贴子pv数缓存key
     */
    public static final String PV_INCR_COUNT = "pv_incr_count:";


    /**
     * 未读回复消息数量
     */
    public static final String MESSAGE_NOT_READ_COUNT_REPLY = "message:not_read_count:reply:";

    /**
     * 未读点赞消息数量
     */
    public static final String MESSAGE_NOT_READ_COUNT_LIKE = "message:not_read_count:like:";

    /**
     * 未读关注消息数量
     */
    public static final String MESSAGE_NOT_READ_COUNT_FOLLOW = "message:not_read_count:follow:";

    /**
     * 未读系统通知数量
     */
    public static final String MESSAGE_NOT_READ_COUNT_SYSTEM = "message:not_read_count:system:";

    /**
     * 未读的消息总数
     */
    public static final String MESSAGE_NOT_READ_COUNT_ALL = "message:not_read_count:all:";
}
