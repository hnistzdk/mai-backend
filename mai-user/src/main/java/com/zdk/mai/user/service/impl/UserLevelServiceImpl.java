package com.zdk.mai.user.service.impl;

import com.zdk.mai.api.post.RemotePostService;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.level.UserLevelModel;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import com.zdk.mai.user.mapper.UserLevelMapper;
import com.zdk.mai.user.service.IUserLevelService;
import com.zdk.mai.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 11:58
 */
@Service
public class UserLevelServiceImpl extends BaseServiceImpl<UserLevelMapper, UserLevelModel> implements IUserLevelService {

    @Autowired
    private IUserService userService;

    @Autowired
    private RemotePostService remotePostService;

    @Override
    public void create(Long userId, String username) {
        UserLevelModel model = new UserLevelModel();
        model.setUserId(userId);
        model.setUsername(username);
        model.setCreateBy(userId);
        model.setCreateTime(new Date());
        model.setUpdateBy(userId);
        model.setUpdateTime(new Date());
        baseMapper.insert(model);
    }

    @Override
    public UserLevelModel getByUserId(Long userId) {
        if (notOk(userId)){
            return null;
        }
        return lambdaQuery()
                .select(UserLevelModel::getLevel,UserLevelModel::getPoints)
                .eq(UserLevelModel::getUserId, userId)
                .one();
    }

    // 经验值 = 贴子的阅读数 / 10 + 获得的点赞数

    @Override
    public void updateAllUserLevel() {

        List<Long> userIdList = userService.list().stream().map(UserModel::getUserId).collect(Collectors.toList());

        Map<Long,Long> pointsMap = new HashMap<>();

        for (Long userId : userIdList) {
            UserAchievementVO data = remotePostService.userAchievement(userId, SecurityConstants.INNER).getData();
            if (data == null){
                continue;
            }
            pointsMap.put(userId, data.getPostReadCount()/10 + data.getPostLikeCount());
        }

        userIdList.forEach(userId ->{
            Long points = pointsMap.getOrDefault(userId, 0L);
            lambdaUpdate().eq(UserLevelModel::getUserId, userId)
                    .set(UserLevelModel::getPoints, points)
                    .set(UserLevelModel::getLevel, getLevel(points))
                    .update();
        });

    }

    private String getLevel(long points){
        if (points >= 0 && points < 100) {
            return "Lv1";
        }
        if (points >= 100 && points < 300) {
            return "Lv2";
        }
        if (points >= 300 && points < 600) {
            return "Lv3";
        }
        if (points >= 600 && points < 1000) {
            return "Lv4";
        }
        if (points >= 1000 && points < 1500) {
            return "Lv5";
        }
        if (points >= 1500) {
            return "Lv6";
        }
        return "Lv1";
    }



}
