package com.zdk.mai.common.security.service;

import cn.hutool.core.util.IdUtil;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.utils.JwtUtil;
import com.zdk.mai.common.core.utils.ServletUtils;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.auth.AuthUtil;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/26 20:26
 */
@Component
public class TokenService {

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    protected static final long EXPIRE_TIME = CacheConstants.EXPIRATION;

    protected static final String ACCESS_TOKEN = CacheConstants.LOGIN_TOKEN_KEY;

    protected static final Long MILLIS_MINUTE_TEN = CacheConstants.REFRESH_TIME * MILLIS_MINUTE;


    @Autowired
    private RedisService redisService;

    /**
     * 生成token并缓存用户信息到redis,然后返回
     *
     * @param loginUserVO
     * @return
     */
    public Map<String, Object> createToken(LoginUserVO loginUserVO) {
        // Jwt存储信息
        Long userId = loginUserVO.getUserId();
        String username = loginUserVO.getUsername();

        //验证成功后删除错误次数的缓存
        if (Boolean.TRUE.equals(redisService.hasKey(getPwdErrKey(username)))) {
            redisService.deleteObject(getPwdErrKey(username));
        }
        loginUserVO.setUserId(userId);
        //生成随机token
        String token = IdUtil.fastUUID();
        loginUserVO.setToken(token);

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(SecurityConstants.USER_KEY, token);
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, userId);
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, username);
        //设置token过期时间为720分钟 即12小时
        //生成jwt 校验的时候联合redis同时校验
        String jwtToken = JwtUtil.createToken(claimsMap, EXPIRE_TIME, ChronoUnit.MINUTES);

        //刷新令牌有效期 并将用户信息存入缓存
        refreshToken(loginUserVO);

        //判断是否是管理员
        boolean admin = AuthUtil.isAdmin(loginUserVO);
        // 接口返回的信息
        Map<String, Object> rspMap = new HashMap<>();
        if (admin){
            rspMap.put("admin", true);
        }
        rspMap.put("accessToken", jwtToken);
        rspMap.put("expiresIn", EXPIRE_TIME);
        rspMap.put("userId", userId);
        rspMap.put("username", username);
        return rspMap;
    }

    /**
     * 获取用户
     *
     * @return 用户信息
     */
    public LoginUserVO getLoginUser() {
        return getLoginUser(ServletUtils.getRequest());
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = SecurityUtils.getToken(request);
        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserVO getLoginUser(String token) {
        LoginUserVO user;
        if (StringUtils.isNotEmpty(token)) {
            String userKey = JwtUtil.getUserKey(token);
            user = redisService.getCacheObject(getTokenKey(userKey));
            return user;
        }
        return null;
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     *
     * @param loginUser
     */
    public void verifyToken(LoginUserVO loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUserVO loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + EXPIRE_TIME * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisService.setCacheObject(userKey, loginUser, EXPIRE_TIME, TimeUnit.MINUTES);
    }


    /**
     * 用户信息缓存key
     *
     * @param token
     * @return
     */
    public String getTokenKey(String token) {
        return ACCESS_TOKEN + token;
    }


    /**
     * 登录错误次数缓存key
     *
     * @param username
     * @return
     */
    private String getPwdErrKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    /**
     * 校验密码错误次数是否超过设定次数
     *
     * @param username
     * @return
     */
    public boolean verifyPwdErrCount(String username) {
        String pwdErrKey = getPwdErrKey(username);
        Integer pwdErrCount = redisService.getCacheObject(pwdErrKey);
        pwdErrCount = pwdErrCount == null ? 0 : pwdErrCount;
        return pwdErrCount >= CacheConstants.PASSWORD_MAX_RETRY_COUNT;
    }

    /**
     * 增加密码错误次数缓存
     *
     * @param username
     */
    public void addPwdErrCount(String username) {
        String pwdErrKey = getPwdErrKey(username);
        Integer pwdErrCount = redisService.getCacheObject(pwdErrKey);
        pwdErrCount = pwdErrCount == null ? 0 : pwdErrCount;
        pwdErrCount += 1;
        redisService.setCacheObject(pwdErrKey, pwdErrCount, CacheConstants.PASSWORD_LOCK_TIME, TimeUnit.MINUTES);
    }


    /**
     * 删除用户缓存信息
     *
     * @param token
     */
    public void deleteLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = JwtUtil.getUserKey(token);
            redisService.deleteObject(getTokenKey(userKey));
        }
    }
}
