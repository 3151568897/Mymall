package com.example.mymall.member.exception;

import com.example.common.exception.BaseCodeEnume;
import com.example.common.exception.LoginException;
import com.example.common.exception.PhoneExistException;
import com.example.common.exception.UsernameExistException;
import com.example.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理所有异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.example.mymall.member.controller")
public class mymallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现异常{}, 异常类型:{}", e.getMessage(), e.getClass(),e);
        log.error(String.valueOf(e));

        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item)->{
            String message = item.getDefaultMessage();
            //错误属性名
            String field = item.getField();
            map.put(field, message);
        });
        return R.error(BaseCodeEnume.VAILD_EXCEPTION.getCode(), BaseCodeEnume.VAILD_EXCEPTION.getMsg()).put("data", map);
    }

    @ExceptionHandler(value = UsernameExistException.class)
    public R handleException(UsernameExistException e){
        log.error("出现异常{}, 异常类型:{}", e.getMessage(), e.getClass());
        log.error(String.valueOf(e));

        return R.error(BaseCodeEnume.USER_EXIST_EXCEPTION.getCode(),BaseCodeEnume.USER_EXIST_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = PhoneExistException.class)
    public R handleException(PhoneExistException e){
        log.error("出现异常{}, 异常类型:{}", e.getMessage(), e.getClass());
        log.error(String.valueOf(e));

        return R.error(BaseCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BaseCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = LoginException.class)
    public R handleException(LoginException e){
        log.error("出现异常{}, 异常类型:{}", e.getMessage(), e.getClass());
        log.error(String.valueOf(e));

        return R.error(BaseCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e){
        log.error("出现异常{}, 异常类型:{}", e.getMessage(), e.getClass());
        log.error(String.valueOf(e));

        return R.error(BaseCodeEnume.UNKNOW_EXCEPTION.getCode(), BaseCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
