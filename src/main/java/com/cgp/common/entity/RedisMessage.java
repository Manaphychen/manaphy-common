package com.cgp.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;


/**
 * 消息模型
 *
 * @author Manaphy
 * @date 2021/02/24
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class RedisMessage {

    /**
     * 消息队列组
     **/
    private String group;

    /**
     * 消息id
     */
    private String id;
    /**
     * 消息延迟 单位:秒
     */
    @NonNull
    private long delay;

    /**
     * 消息存活时间 单位:秒
     */
    @NonNull
    private long ttl;
    /**
     * 消息体，对应业务内容
     */
    private String body;
    /**
     * 创建时间，如果只有优先级没有延迟，可以设置创建时间为0
     * 用来消除时间的影响
     */
    private long createTime;
}
