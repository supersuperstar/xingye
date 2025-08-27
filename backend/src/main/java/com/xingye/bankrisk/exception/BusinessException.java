package com.xingye.bankrisk.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常类
 * 用于业务逻辑层面的异常处理
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, String code) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, String code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = "BUSINESS_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
