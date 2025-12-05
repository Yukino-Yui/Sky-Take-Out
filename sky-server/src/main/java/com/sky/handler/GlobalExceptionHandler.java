package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 * Controller 调用 Service → Service 抛异
 * 不能让异常直接返回给前端
 * 必须使用统一格式（Result）封装输出
 * 用全局异常处理器最方便
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     * Spring MVC 拦截所有抛到 Controller 层外的异常
     * ➤ @RestControllerAdvice 注册了全局异常处理机制
     * ➤ @ExceptionHandler 根据异常类型进行匹配
     * ➤ 处理完后封装到Result对象中的error，在最后统一返回给前端
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获新增员工异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String str = ex.getMessage();
        String msg = null;
        if (str.contains("Duplicate entry")) {
            String[] s = str.split(" ");
            String str1 = s[2];
            msg = str1 + MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        }
       else {
            msg = MessageConstant.UNKNOWN_ERROR;
            return Result.error(msg);
        }
    }

}
