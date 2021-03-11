package com.cgp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果代码
 *
 * @author Manaphy
 * @date 2020-07-23
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    /**
     * 成功状态码
     */
    SUCCESS(200, "请求成功"),
    /**
     * 失败状态码
     */
    FAILED(400, "请求失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    NULL_POINTER(402, "空指针异常"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),
    EXCEPTION(500, "系统异常");
    private final int code;
    private final String message;
}
