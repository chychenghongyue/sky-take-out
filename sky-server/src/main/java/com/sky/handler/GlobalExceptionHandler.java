package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result<T> exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理sql异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result<T> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //Duplicate entry 'temp' for key 'employee.idx_username'
        String Message = ex.getMessage();
        if (Message.contains("Duplicate entry")) {
            String[] split = Message.split(" ");//按照空格分割
            String user_name = split[2];//用户名信息在数组的第三个元素
            String mes = user_name + MessageConstant.USER_EXISTS;
            log.error("异常信息：{}", mes);
            return Result.error(mes);
        } else
            return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
