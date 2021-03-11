package com.cgp.common.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * Redis 分布式锁实现
 *
 * @author Manaphy
 * @date 2020-04-28
 */
@Service
public class RedisLock {

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";

    /**
     * 释放锁脚本
     * if get(key) == value return del(key)
     */
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 对于 Redis 集群则无法使用
     * <p>
     * 支持重复，线程安全
     *
     * @param lockKey  加锁键
     * @param clientId 加锁客户端唯一标识(采用UUID)
     * @param seconds  锁过期时间
     * @return Boolean
     */
    public Boolean tryLock(String lockKey, String clientId, long seconds) {
        return stringRedisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            //String result = jedis.set(lockKey, clientId, "NX", "EX", seconds); //Jedis 3.1.0之前使用该方法
            String result = jedis.set(lockKey, clientId, new SetParams().nx().px(seconds));
            return LOCK_SUCCESS.equals(result);
        });
    }

    /**
     * 与 tryLock 相对应，用作释放锁
     *
     * @param lockKey  锁定键
     * @param clientId 客户端Id
     */
    public void releaseLock(String lockKey, String clientId) {
        stringRedisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            Object result = jedis.eval(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey),
                    Collections.singletonList(clientId));
            return RELEASE_SUCCESS.equals(result);
        });
    }
}
