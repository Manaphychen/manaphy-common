package com.cgp.common.handler;

import com.cgp.common.entity.ApiResult;
import com.cgp.common.exception.CustomException;
import com.cgp.common.exception.FrequentRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义统一异常拦截类
 * RestControllerAdvice->声明在类上用于指定该类为控制增强器类。并声明返回的结果为 RESTFull 风格的数据
 * ExceptionHandler->声明在方法上用于指定需要统一拦截的异常。
 *
 * @author Manaphy
 */
@RestControllerAdvice
@Slf4j
@ConditionalOnProperty(prefix = "manaphy", name = "exception", havingValue = "true", matchIfMissing = true)
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public ApiResult handleException(CustomException e) {
        // 打印异常信息
        log.error("自定义异常:{}", e.getMessage());
        return ApiResult.failed(e.getMessage());
    }

    /**
     * 处理重复请求异常
     */
    @ExceptionHandler(FrequentRequestsException.class)
    public ApiResult handleFrequentRequestsException(FrequentRequestsException e) {
        // 打印异常信息
        log.error("频繁请求异常:{}", e.getMessage());
        return ApiResult.exception(e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ApiResult handleNullPointerException(NullPointerException e) {
        // 打印异常信息
        log.error("空指针异常", e);
        log.error(e.toString());
        return ApiResult.nullPointer("空指针异常,请联系管理员处理");
    }

    /**
     * 参数错误异常
     *
     * @param e 异常
     * @return {@link ApiResult}
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public ApiResult handleValidException(Exception e) {
        log.error("堆栈信息:", e);
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        Map<String, String> errorMap = new HashMap<>(16);
        assert bindingResult != null;
        bindingResult.getFieldErrors().forEach((fieldError) -> {
                    log.error("请求参数错误：{},field{},errorMessage{}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                }

        );
        return ApiResult.validateFailed(errorMap);
    }


    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handleOtherException(Exception e) {
        if (e instanceof ValidationException) {
            log.error("校验失败异常:{}", e.getMessage());
            return ApiResult.validateFailed(e.getMessage());
        }
        // 打印异常信息
        log.error("不可知的异常:{}", e.getMessage());
        //打印异常堆栈信息
        log.error("不可知异常的堆栈信息:", e);
        return ApiResult.exception(e.getMessage());
    }

}
