package com.zdk.mai.user.service.impl;

import cn.hutool.core.lang.UUID;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.comment.RemoteCommentService;
import com.zdk.mai.api.file.RemoteFileService;
import com.zdk.mai.api.post.RemotePostService;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.CommonConstants;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.level.UserLevelModel;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.request.*;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.user.mapper.UserMapper;
import com.zdk.mai.user.service.IUserFollowService;
import com.zdk.mai.user.service.IUserLevelService;
import com.zdk.mai.user.service.IUserService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 16:38
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, UserModel> implements IUserService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IUserLevelService userLevelService;

    @Autowired
    private IUserFollowService userFollowService;

    @Autowired
    private RemoteCommentService remoteCommentService;

    @Autowired
    private RemotePostService remotePostService;

    @Autowired
    private RemoteFileService remoteFileService;



    @Override
    public UserModel selectUserByUsername(String username) {
        UserModel userModel = lambdaQuery().eq(UserModel::getUsername, username).one();
        if (userModel == null) {
            throw new BusinessException(ErrorEnum.USER_NOTEXIST_ERROR.getCode(),ErrorEnum.USER_NOTEXIST_ERROR.getMsg());
        }
        return userModel;
    }

    @Override
    public Long create(UserCreateRequest userCreateRequest) {
        String username = userCreateRequest.getUsername();
        String password = userCreateRequest.getPassword();
        // 再检查一遍重复用户
        if (Boolean.TRUE.equals(redisService.hasKey(CacheConstants.USER_EXIST_KEY + username))) {
            throw new BusinessException(ErrorEnum.USER_DUPLICATION_ERROR.getCode(),ErrorEnum.USER_DUPLICATION_ERROR.getMsg());
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userCreateRequest, userModel);
        //加密密码
        String encryptPassword = SecurityUtils.encryptPassword(password);
        userModel.setPassword(encryptPassword);
        //插入数据
        baseMapper.insert(userModel);
        //缓存已注册用户
        redisService.setCacheObject(CacheConstants.USER_EXIST_KEY + username, username);

        //新增用户等级初始数据
        userLevelService.create(userModel.getUserId(), username);

        return userModel.getUserId();
    }


    @Override
    public void update(UserUpdateRequest updateRequest) {
        Long userId = updateRequest.getUserId();
        UserInfoVO profile = this.profile(userId);
        if (!userId.equals(profile.getUserId())){
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_MODIFICATION.getCode(), ErrorEnum.UNAUTHORIZED_MODIFICATION.getMsg());
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(updateRequest, userModel);

        //处理管理员重置密码
        String password = updateRequest.getPassword();
        if (isOk(password)){
            String encryptPwd = SecurityUtils.encryptPassword(password);
            userModel.setPassword(encryptPwd);
        }
        baseMapper.updateById(userModel);
    }

    @Override
    public UserInfoVO profile(Long userId) {
        UserModel model = getById(userId);
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(model, vo);

        //填充等级信息
        UserLevelModel userLevelModel = userLevelService.getByUserId(userId);
        vo.setLevel(userLevelModel.getLevel());
        vo.setPoints(userLevelModel.getPoints());

        //统计获赞、贴子总pv、point
        CommonResult<UserAchievementVO> postRes = remotePostService.userAchievement(userId, SecurityConstants.INNER);
        CommonResult<UserAchievementVO> commentRes = remoteCommentService.userAchievement(userId, SecurityConstants.INNER);

        if (CommonResult.isSuccess(postRes) && CommonResult.isSuccess(commentRes)){
            UserAchievementVO postResData = postRes.getData();
            UserAchievementVO commentResData = commentRes.getData();
            vo.setLikeCount(postResData.getPostLikeCount() + commentResData.getCommentLikeCount());
            vo.setReadCount(postResData.getPostReadCount());
        }
        // 获取关注关系 isFollow
        Boolean follow = userFollowService.isFollow(userId);
        vo.setIsFollow(follow);
        return vo;
    }

    @SneakyThrows
    @Override
    public Boolean uploadAvatar(MultipartFile multipartFile, String base) {

        String filename = multipartFile.getOriginalFilename();
        byte[] fileBytes = multipartFile.getBytes();

        String fileType = StringUtils.substringAfterLast(filename, ".");
        String randomFilename = UUID.fastUUID().toString();
        filename = randomFilename + "." +fileType;

        //上传
        CommonResult upload = remoteFileService.upload(fileBytes, filename, base, SecurityConstants.INNER);

        if (CommonResult.isSuccess(upload)){
            String avatarUrl = CommonConstants.DOMAIN_PREFIX + base + filename;
            lambdaUpdate().eq(UserModel::getUserId, SecurityUtils.getUserId())
                    .set(UserModel::getAvatar, avatarUrl).update();
            //将头像链接缓存存入redis
            redisService.setCacheObject(CacheConstants.USER_AVATAR_KEY + SecurityUtils.getUserId(), avatarUrl);
            return true;
        }
        return false;
    }

    @Override
    public String getAvatar(Long userId) {
        String avatar = redisService.getAvatar(userId);
        if (notOk(avatar)) {
            UserModel model = lambdaQuery().select(UserModel::getAvatar).eq(UserModel::getUserId, userId).one();
            if (model != null){
                avatar = model.getAvatar();
            }
        }
        return avatar;
    }


    @Override
    public void bindEmail(BindEmailRequest bindEmailRequest) {
        String email = bindEmailRequest.getEmail();
        String code = bindEmailRequest.getCode();
        if (emailExist(email)){
            throw new BusinessException(ErrorEnum.EMAIL_DUPLICATION_ERROR.getCode(), ErrorEnum.EMAIL_DUPLICATION_ERROR.getMsg());
        }
        Long userId = SecurityUtils.getUserId();
        String cacheCode = redisService.getCacheObject(CacheConstants.EMAIL_CODE_KEY + CommonConstants.EMAIL_CODE_TYPE_BIND + email);
        if (code.equals(cacheCode)){
            lambdaUpdate()
                    .set(UserModel::getEmail, email)
                    .eq(UserModel::getUserId, userId)
                    .update();
            redisService.setCacheObject(CacheConstants.EMAIL_EXIST_KEY + email,email);
        }else {
            throw new BusinessException(ErrorEnum.VERIFICATION_CODE_ERROR.getCode(),ErrorEnum.VERIFICATION_CODE_ERROR.getMsg());
        }
    }


    @Override
    public void unbindEmail() {
        Long userId = SecurityUtils.getUserId();
        UserModel model = lambdaQuery().select(UserModel::getEmail)
                .eq(UserModel::getUserId, userId)
                .one();
        String email = model.getEmail();
        //将邮箱设为空
        lambdaUpdate()
                .set(UserModel::getEmail, null)
                .eq(UserModel::getUserId, userId)
                .update();
        //清除缓存
        redisService.deleteObject(CacheConstants.EMAIL_EXIST_KEY + email);
        // TODO: 2023/3/17 记录操作日志
    }

    @Override
    public Boolean disabledPost(String username, boolean value) {
        return lambdaUpdate()
                .eq(UserModel::getUsername, username)
                .eq(UserModel::getPostDisabled, value)
                .set(UserModel::getPostDisabled, !value)
                .update();
    }

    @Override
    public Boolean disabledComment(String username, boolean value) {
        return lambdaUpdate()
                .eq(UserModel::getUsername, username)
                .eq(UserModel::getCommentDisabled, value)
                .set(UserModel::getCommentDisabled, !value)
                .update();
    }

    /**
     * 判断邮箱是否已被绑定 true:
     * @param email
     * @return
     */
    public Boolean emailExist(String email){
        Boolean exist = redisService.hasKey(CacheConstants.EMAIL_EXIST_KEY + email);
        if (exist){
            return true;
        }
        UserModel model = lambdaQuery().select(UserModel::getEmail)
                .eq(UserModel::getEmail, email)
                .one();
        return model != null && isOk( model.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        String oldPassword = request.getOldPassword();

        String newPassword = request.getNewPassword();

        UserModel userModel = lambdaQuery().select(UserModel::getPassword).eq(UserModel::getUserId, SecurityUtils.getUserId()).one();

        if (!SecurityUtils.matchPassword(oldPassword,userModel.getPassword())){
            throw new BusinessException(ErrorEnum.OLD_PASSWORD_ERROR.getCode(),ErrorEnum.OLD_PASSWORD_ERROR.getMsg());
        }

        String encryptPassword = SecurityUtils.encryptPassword(newPassword);

        lambdaUpdate().set(UserModel::getPassword,encryptPassword).eq(UserModel::getUserId, SecurityUtils.getUserId()).update();

    }

    @Override
    public void emailResetPassword(EmailResetPasswordRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String newPassword = request.getNewPassword();

        String cacheCode = redisService.getCacheObject(CacheConstants.EMAIL_CODE_KEY + CommonConstants.EMAIL_CODE_TYPE_RESET + email);

        if (!cacheCode.equals(code)){
            throw new BusinessException("验证码错误或已过期");
        }

        String encryptPassword = SecurityUtils.encryptPassword(newPassword);

        lambdaUpdate().set(UserModel::getPassword,encryptPassword).eq(UserModel::getEmail, email).update();
    }

    @Override
    public PageInfo<UserModel> list(UserSearchRequest searchRequest) {

        String keywords = searchRequest.getKeywords();

        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());

        List<UserModel> list = lambdaQuery()
                .like(isOk(keywords), UserModel::getUsername,keywords)
                .or()
                .like(isOk(keywords), UserModel::getNickname,keywords)
                .list();

        return new PageInfo<>(list);

    }
}
