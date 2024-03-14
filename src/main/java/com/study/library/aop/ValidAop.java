package com.study.library.aop;

import com.study.library.exception.ValidException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class ValidAop {

    @Pointcut("@annotation(com.study.library.aop.annotation.ValidAspect)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // bindingResult 매개변수로 꺼내기
        Object[] args = proceedingJoinPoint.getArgs();

        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) { // Object는 최상위 이므로 다운캐스팅 해줘야 함
            if(arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg;
            }
//            System.out.println(args.getClass()); // 클래스 이름이 BeanPropertyBindingResult 임
        }

        if (bindingResult.hasErrors()) { // error가 있다면, 그 error들을 list로 가져옴
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField(); // DTO 변수명
                String message = fieldError.getDefaultMessage(); // 메세지 내용
                errorMap.put(fieldName, message);
            }
//            return ResponseEntity.badRequest().body(errorMap); Advice에서 대신 해줌
            throw new ValidException(errorMap);
        }

        return proceedingJoinPoint.proceed();

    }
}
