package com.example.lock.core;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/* *
 *  redisson 分布式锁
 **/
@Slf4j
@Component
public class RedisDistributedLock {

    @Autowired
    private RedissonClient redissonClient;

    private static final RedisLock FAILURE_LOCK = new RedisLock();

    /**
     * 尝试获取锁
     *
     * @param objectName
     * @return
     */
    public RedisLock tryLock(String objectName) {
        RLock rLock = redissonClient.getLock(objectName);
        if (rLock.tryLock()) {
            return new RedisLock(rLock, true);
        }
        return FAILURE_LOCK;
    }

    /**
     * 获取锁
     *
     * @param objectName
     * @param waitTime   等待获取锁时长
     * @param unit       时间单位
     * @return
     */
    public RedisLock tryLock(String objectName, long waitTime, TimeUnit unit) {
        RLock rLock = redissonClient.getLock(objectName);
        boolean lockSuccess = false;
        try {
            lockSuccess = rLock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            return FAILURE_LOCK;
        }
        if (lockSuccess) {
            return new RedisLock(rLock, true);
        }
        return FAILURE_LOCK;
    }


    /**
     * 尝试获取锁，推荐此方法，防止死锁
     *
     * @param objectName 锁对象
     * @param waitTime   获取锁最多等待时间
     * @param leaseTime  持有锁时间
     * @param unit       时间单位
     * @return
     */
    public RedisLock tryLock(String objectName, long waitTime, long leaseTime, TimeUnit unit) {
        RLock rLock = redissonClient.getLock(objectName);
        boolean lockSuccess = false;
        try {
            lockSuccess = rLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return FAILURE_LOCK;
        }
        if (lockSuccess) {
            return new RedisLock(rLock, true);
        }
        return FAILURE_LOCK;
    }


    /**
     * 获取锁
     *
     * @param objectName
     * @return
     */
    public RedisLock lock(String objectName) {
        RLock rLock = redissonClient.getLock(objectName);
        try {
            rLock.lock();
            return new RedisLock(rLock, true);
        } catch (Exception e) {
            log.error("RedisDistributedLock lock() failed, objectName=" + objectName, e);
        }
        return FAILURE_LOCK;
    }

    /**
     * 获取锁
     *
     * @param objectName
     * @return
     */
    public RedisLock lock(String objectName, long leaseTime, TimeUnit unit) {
        RLock rLock = redissonClient.getLock(objectName);
        try {
            if (rLock.isLocked()) {
                return FAILURE_LOCK;
            }
            rLock.lock(leaseTime, unit);
            return new RedisLock(rLock, true);
        } catch (Exception e) {
            log.error("RedisDistributedLock lock() failed, objectName=" + objectName, e);
        }
        return FAILURE_LOCK;
    }

    /**
     * 释放锁
     *
     * @param objectName
     * @return
     */
    public void unlock(String objectName) {
        RLock rLock = redissonClient.getLock(objectName);
        if (rLock == null) {
            return;
        }
        // 如果锁被当前线程持有，则释放
        if (rLock.isHeldByCurrentThread()) {
            rLock.unlock();
        }
    }

    /**
     * 判断对象是否被锁
     *
     * @param objectName 锁对象
     * @return
     */
    public boolean isLocked(String objectName) {
        if (null == objectName) {
            return false;
        }
        return redissonClient.getLock(objectName).isLocked();
    }

    /**
     * 强制删除对象锁，小心使用,注意时间差
     *
     * @param objectName 锁对象
     */
    public void delLock(String objectName) {
        if (null == objectName) {
            return;
        }
        RLock rLock = redissonClient.getLock(objectName);
        if (rLock.isLocked()) {
            rLock.forceUnlockAsync();
        }
    }


}
