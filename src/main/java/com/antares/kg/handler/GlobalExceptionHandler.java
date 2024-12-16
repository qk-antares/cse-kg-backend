package com.antares.kg.handler;

import com.antares.kg.exception.BusinessException;
import com.antares.kg.model.enums.HttpCodeEnum;
import com.antares.kg.model.dto.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ConstraintViolationException e){
        for(ConstraintViolation<?> s:e.getConstraintViolations()){
            return s.getInvalidValue()+": "+s.getMessage();
        }
        return "请求参数不合法";
    }

    @ExceptionHandler(Exception.class)
    public R<Void> exceptionHandler(Exception e){
        //打印异常信息
        log.error("出现未知异常！", e);
        //从异常对象中获取信息，封装成ResponseResult后返回
        return R.error(HttpCodeEnum.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }
}
