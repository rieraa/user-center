package com.ame.usercenter.exception;


import com.ame.usercenter.common.BaseResponse;
import com.ame.usercenter.common.ErrorCode;
import com.ame.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 *
 * @author ame
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //该注解中的参数作用：只去捕获指定的异常此处为BusinessException
    //针对什么样的异常做什么样的处理
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.error("BusinessException" + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());

    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse businessExceptionHandler(RuntimeException e) {
        //集中记录日志
        log.error("runtimeException", e);
        //系统内部抛出的异常
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");


    }
}

