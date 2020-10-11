package com.example.lock.sample;

import com.example.lock.core.RedisDistributedLock;
import com.example.lock.core.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>Description: </p>
 *
 * @author zhaoyl
 * @version v1.0
 * @date 2020-10-11 22:16
 */
@Service
public class LockDemo {

    @Autowired
    private RedisDistributedLock redisDistributedLock;

    public void lockTest() {
        final RedisLock redisLock = redisDistributedLock.tryLock("testLOck", 3 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);
        try {
            if (!redisLock.isLockSuccess()) {
                //获取锁失败的处理
                //此处可以抛出异常，或是返回错误信息，如：什么什么正在被处理等
                return;
            }
            //TODO 业务逻辑
        } finally {
            redisLock.unlock();
        }
    }

}
