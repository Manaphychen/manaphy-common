package com.cgp.common.queue;

import com.cgp.common.entity.RedisMessage;
import com.cgp.common.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis消息队列
 *
 * @author Manaphy
 * @date 2021/02/24
 */
@Component
public class RedisMq {
    /**
     * 消息池前缀，以此前缀加上传递的消息id作为key，以消息{@link RedisMessage}的消息对象作为值存储
     */
    public static final String MSG_POOL = "Message:Pool:";
    /**
     * zset队列名称 queue
     */
    public static final String QUEUE_NAME = "Message:Queue:";

    @Resource
    private RedisService redisService;
    @Resource
    private RedisTemplate<String, RedisMessage> redisTemplate;

    /**
     * 存入消息池
     *
     * @param message 消息
     */
    public void addMsgPool(RedisMessage message) {
        if (message == null) {
            return;
        }
        redisTemplate.opsForValue().setIfAbsent(MSG_POOL + message.getGroup() + message.getId(),
                message, message.getTtl(), TimeUnit.DAYS);
    }

    /**
     * 从消息池中删除消息
     *
     * @param id id
     */
    public void delMsgPool(String group, String id) {
        redisService.delete(MSG_POOL + group + id);
    }

    /**
     * 向队列中添加消息
     *
     * @param key   key
     * @param score 优先级
     * @param value 值
     */
    public void addMsg(String key, String value, long score) {
        redisService.zAdd(key, value, score);
    }

    /**
     * 从队列删除消息
     *
     * @param key key
     * @param id  id
     */
    public void delMsg(String key, String id) {
        redisService.zRem(key, id);
    }

}
