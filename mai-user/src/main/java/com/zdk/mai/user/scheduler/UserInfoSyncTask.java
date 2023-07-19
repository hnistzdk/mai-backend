package com.zdk.mai.user.scheduler;

import com.zdk.mai.common.es.mapper.EsUserMapper;
import com.zdk.mai.common.es.model.EsUserModel;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.redis.service.RedissonService;
import com.zdk.mai.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/7 19:37
 */
@Slf4j
@Component
public class UserInfoSyncTask {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedissonService redissonService;


    @Resource
    private EsUserMapper esUserMapper;

    /**
     * 每小时执行一次
     */
    @Async("mailTaskExecutor")
    @Scheduled(cron = "0 0 */1 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void execute() {

        log.info("更新同步用户信息到es任务 开始执行");

        executeSync();
    }

    private void executeSync() {
        //获取一个公平锁

        RLock lock = redissonService.getLock("user_info_es_sync_task" + UUID.randomUUID(), true);

        try {
            // 拿到锁 执行任务
            boolean b = lock.tryLock();
            if (b) {
                List<UserModel> list = userService.list();

                List<EsUserModel> esUserModelList = new ArrayList<>(list.size());

                for (UserModel userModel : list) {
                    EsUserModel esUserModel = new EsUserModel();
                    BeanUtils.copyProperties(userModel, esUserModel);
                    esUserModelList.add(esUserModel);
                }

                esUserMapper.insertBatch(esUserModelList);

            }
        } catch (Exception e) {
            log.error("UserInfoTask failed!", e);
        } finally {
            // 如果是由当前线程持有 and 锁住状态 解锁
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
            }
        }
        log.info("更新同步用户信息到es任务 执行完毕");
    }
}
