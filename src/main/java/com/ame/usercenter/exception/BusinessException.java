package com.ame.usercenter.exception;

import com.ame.usercenter.common.ErrorCode;

/**
 * 自定义异常类
 * 扩充了原本的RuntimeException
 * 使其能够支持另外两个新的字段
 * @author ame
 */

public class BusinessException extends RuntimeException {
    //RuntimeException 运行时异常 不在JAVA中显式捕获 不需要throw

    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;

    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
