package com.antares.kg.handler;

import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public R<Void> systemExceptionHandler(BusinessException e){
        //打印异常信息
        log.error("出现异常：[{}]，原因：[{}]", e.getClass().getName(), e.getMessage());
        //从异常对象中获取信息，封装成ResponseResult后返回
        return R.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public R<Void> exceptionHandler(Exception e){
        //打印异常信息
        log.error("出现未知异常！", e);
        //从异常对象中获取信息，封装成ResponseResult后返回
        return R.error(HttpCodeEnum.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }
}
