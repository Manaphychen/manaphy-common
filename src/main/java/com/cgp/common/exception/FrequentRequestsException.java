package com.cgp.common.exception;

/**
 * 访问频繁异常类
 *
 * @author Manaphy
 */
public class FrequentRequestsException extends RuntimeException {

    public FrequentRequestsException() {
        super();
    }

    public FrequentRequestsException(String message) {
        super(message);
    }

    public FrequentRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrequentRequestsException(Throwable cause) {
        super(cause);
    }

    protected FrequentRequestsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
