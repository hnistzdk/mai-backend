package com.zdk.mai.user.scheduler;

import com.zdk.mai.common.redis.service.RedissonService;
import com.zdk.mai.user.service.IUserLevelService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/4 17:34
 */
@Slf4j
@Component
public class UserLevelTask {

    @Autowired
    private IUserLevelService userLevelService;

    @Autowired
    private RedissonService redissonService;


    /**
     * 每小时执行一次
     */
    @Async("mailTaskExecutor")
    @Scheduled(cron = "0 0 */1 * * ?")
    public void execute() {

        log.info("更新用户等级信息任务 开始执行");

        executeLevelUpdate();
    }

    private void executeLevelUpdate() {

        //获取一个公平锁

        RLock lock = redissonService.getLock("user_level_points_task" + UUID.randomUUID(), true);

        try {
            // 拿到锁 执行任务
            boolean b = lock.tryLock();
            if (b) {
                // 更新所有用户的等级信息
                userLevelService.updateAllUserLevel();
            }
        } catch (Exception e) {
            log.error("UserLevelTask failed!", e);
        } finally {
            // 如果是由当前线程持有 and 锁住状态 解锁
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
            }
        }
        log.info("用户等级信息任务 执行完毕");
    }

}
