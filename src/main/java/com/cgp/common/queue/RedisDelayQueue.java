package com.cgp.common.queue;

import com.cgp.common.entity.RedisMessage;
import com.cgp.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * redis延迟队列
 *
 * @author Manaphy
 * @date 2021/02/24
 */
@Slf4j
@Component
public class RedisDelayQueue {
    @Resource
    private RedisMq redisMq;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisTemplate<String, RedisMessage> redisTemplate;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 消息生产者
     *
     * @param message 消息
     */
    public void sendMessage(@Validated RedisMessage message) {
        Assert.notNull(message, "消息不能为空");
        // 将有效信息放入消息队列和消息池中
        message.setId(UUID.randomUUID().toString());
        long delayTime = message.getCreateTime() + message.getDelay() * 1000;
        try {
            // 将消息放入消息池
            redisMq.addMsgPool(message);
            // 将消息加入延迟队列
            redisMq.addMsg(RedisMq.QUEUE_NAME + message.getGroup(), message.getId(), delayTime);
            log.info("RedisMq发送消费信息{}，当前时间:{},消费时间预计为:{}", message.getBody(), sdf.format(new Date()), sdf.format(delayTime));
        } catch (Exception e) {
            log.info("RedisMq 消息发送失败，当前时间:{}", new Date());
        }
    }

    /**
     * 消息消费者
     * 可以使用Cron表达式代替线程循环来执行定期任务
     *
     * @param execute 消息执行类
     */
    public void monitor(RedisMqExecute execute) {
        String queueName = RedisMq.QUEUE_NAME + execute.getQueueName();
        // 从延迟队列中获取已超时的消息
        Set<String> set = redisService.zRangeByScore(queueName, 0, System.currentTimeMillis());
        if (set.size() == 0) {
            return;
        }
        long current = System.currentTimeMillis();
        for (String id : set) {
            long score = redisService.zScore(queueName, id).longValue();
            if (current < score) {
                return;
            }
            // 已超时的消息拿出来消费
            RedisMessage message = null;
            String msgPool = RedisMq.MSG_POOL + execute.getQueueName();
            try {
                message = redisTemplate.opsForValue().get(msgPool + id);
                if (message == null) {
                    return;
                }
                log.info("RedisMq:{},获取消息成功,时间为:{}", message.getBody(), sdf.format(new Date()));
                // 处理获取的消息
                execute.execute(message);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("消费异常，重新回到队列");
                sendMessage(message);
            } finally {
                redisMq.delMsg(queueName, id);
                redisMq.delMsgPool(execute.getQueueName(), id);
            }
        }

    }
}
