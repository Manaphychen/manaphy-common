package com.cgp.common.aspect;

import com.cgp.common.annotation.NoConcurrentSubmit;
import com.cgp.common.exception.FrequentRequestsException;
import com.cgp.common.service.RedisLock;
import com.cgp.common.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * AOP类解析注解-配合redis-解决程序集群部署时请求可能会落到多台机器上的问题。
 * 作用：对标记了@NoConcurrentSubmit的方法进行拦截
 *
 * @author Manaphy
 * @date 2020-04-28
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(RedisLock.class)
public class NoConcurrentSubmitAspect {

    @Resource
    private RedisLock redisLock;

    @Bean
    @ConditionalOnMissingBean
    public RedisLock redisLock() {
        return new RedisLock();
    }

    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoConcurrentSubmit noRepeatSubmit) {
    }

    /**
     * 在业务方法执行前，获取当前用户的 token（或者JSessionId）+ 当前请求地址，作为一个唯一 KEY，
     * 去获取 Redis 分布式锁（如果此时并发获取，只有一个线程会成功获取锁。）
     *
     * @param pjp            pjp
     * @param noRepeatSubmit 没有重复提交
     * @return {@link Object}
     * @throws Throwable throwable
     */

    @Around(value = "pointCut(noRepeatSubmit)", argNames = "pjp,noRepeatSubmit")
    public Object around(ProceedingJoinPoint pjp, NoConcurrentSubmit noRepeatSubmit) throws Throwable {
        int lockSeconds = noRepeatSubmit.lockTime();
        //从 AOP 中获取 HttpServletRequest
        HttpServletRequest request = WebUtil.getRequest();
        Assert.notNull(request, "request can not null");
        // 此处可以用token或者JSessionId
        String token = request.getHeader("Authorization");
        String path = request.getServletPath();
        String key = getKey(token, path);
        String clientId = getClientId();

        boolean isSuccess = redisLock.tryLock(key, clientId, lockSeconds);
//        log.info("tryLock key = {}, clientId = {}", key, clientId);
        // 主要逻辑
        if (isSuccess) {
//            log.info("获取锁成功, key = {}, clientId = {}", key, clientId);
            // 获取锁成功
            Object result;
            try {
                // 执行进程
                result = pjp.proceed();
            } finally {
                // 解锁
                redisLock.releaseLock(key, clientId);
//                log.info("释放锁成功, key = {}, clientId = {}", key, clientId);
            }
            return result;
        } else {
            // 获取锁失败，认为是重复提交的请求。
            throw new FrequentRequestsException("重复请求，请稍后再试");
        }
    }


    /**
     * token（或者JSessionId）+ 当前请求地址，作为一个唯一KEY
     *
     * @param token 令牌
     * @param path  路径
     * @return {@link String}
     */
    private String getKey(String token, String path) {
        return token + path;
    }


    /**
     * 生成uuid
     *
     * @return {@link String}
     */
    private String getClientId() {
        return UUID.randomUUID().toString();
    }
}
