package com.zdk.mai.common.redis.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description Redisson操作简单封装
 * @Author zdk
 * @Date 2023/3/21 20:05
 */
@Component
public class RedissonService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取一个锁
     * @param lockKey key
     * @param fair 是否获取公平锁
     * @return
     */
    public RLock getLock(String lockKey, boolean fair){
        return fair ? redissonClient.getFairLock(lockKey) : redissonClient.getLock(lockKey);
    }


    /**
     * 加锁不设置超时时间(拿不到lock线程会一直block)
     * @param lockKey
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     *
     * @param lockKey
     * @param leaseTime 加锁时间，单位为秒
     * @return
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 加锁
     * @param lockKey
     * @param unit
     * @param timeout
     * @return
     */
    public RLock lock(String lockKey, TimeUnit unit, long timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param unit
     * @param waitTime 获取锁等待时间
     * @param leaseTime 加锁时间
     * @return
     */
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 通过lockKey解锁
     * @param lockKey
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 直接通过锁解锁
     * @param lock
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }

}
