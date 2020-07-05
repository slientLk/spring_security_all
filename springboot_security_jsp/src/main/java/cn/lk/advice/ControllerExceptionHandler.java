package cn.lk.advice;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler
    public String exceptionHandler(Exception e){
        //权限不足
        if (e instanceof AccessDeniedException){
            return "redirect:/403.jsp";
        }
        return "redirect:/500.jsp";
    }
}
