package com.cgp.common.aspect;

import com.cgp.common.annotation.IdempotentSubmit;
import com.cgp.common.exception.FrequentRequestsException;
import com.cgp.common.utils.ResubmitLock;
import com.cgp.common.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 幂等切面
 *
 * @author Manaphy
 * @date 2020-08-27
 */
@Slf4j
@Aspect
@Component
public class IdempotentSubmitAspect {

    @Pointcut("@annotation(idempotentSubmit)")
    public void pointCut(IdempotentSubmit idempotentSubmit) {
    }

    @Around(value = "pointCut(idempotentSubmit)", argNames = "pjp,idempotentSubmit")
    public Object around(ProceedingJoinPoint pjp, IdempotentSubmit idempotentSubmit) throws Throwable {
        int seconds = idempotentSubmit.delaySeconds();
        //从 AOP 中获取 HttpServletRequest
        HttpServletRequest request = WebUtil.getRequest();
        Assert.notNull(request, "request can not null");
        Object[] pointArgs = pjp.getArgs();
        List<Object> objects = Arrays.asList(pointArgs);
        String collect = objects.stream().map(String::valueOf).collect(Collectors.joining());
        String key = ResubmitLock.handleKey(collect);
        boolean lock = false;
        try {
            // 对key进行上锁，上锁成功进行下一步业务，失败则进入重复提交业务异常
            lock = ResubmitLock.getInstance().lock(key, new Object());
            if (lock) {
                return pjp.proceed();
            } else {
                throw new FrequentRequestsException("重复请求，请稍后再试");
            }
        } finally {
            // 加锁成功后需要在一定时间后解锁该key，设置解锁key和解锁时间
            ResubmitLock.getInstance().unLock(lock, key, seconds);
        }
    }
}
