package com.cgp.common.queue;

import com.cgp.common.entity.RedisMessage;

/**
 * Redis消息执行接口
 *
 * @author Manaphy
 * @date 2021/02/24
 */
public interface RedisMqExecute {

    /**
     * 获得队列名称
     *
     * @return {@link String}
     */
    String getQueueName();

    /**
     * 统一的通过执行期执行
     *
     * @param message 消息
     */
    void execute(RedisMessage message);

    /**
     * 加入队列
     *
     * @param msg   消息
     * @param delay 延迟时间
     */
    void addQueue(String msg, long delay);

    /**
     * 轮询线程
     */
    void threadPolling();
}
