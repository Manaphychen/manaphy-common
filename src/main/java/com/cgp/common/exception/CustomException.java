package com.cgp.common.exception;

import lombok.Getter;

/**
 * 自定义异常
 *
 * @author Manaphy
 * @date 2020-05-21
 */
@Getter
public class CustomException extends RuntimeException {

    private Integer code;

    private final String message;

    public CustomException(String message) {
        this.message = message;
    }

    public CustomException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }


}