package com.study.library.aop;

import com.study.library.dto.SignupReqDto;
import com.study.library.exception.ValidException;
import com.study.library.repository.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class ValidAop {

    @Autowired
    private UserMapper userMapper;

    @Pointcut("@annotation(com.study.library.aop.annotation.ValidAspect)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();

        // bindingResult 매개변수로 꺼내기
        Object[] args = proceedingJoinPoint.getArgs();

        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) { // Object는 최상위 이므로 다운캐스팅 해줘야 함
            if(arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg;
            }
//            System.out.println(args.getClass()); // 클래스 이름이 BeanPropertyBindingResult 임
        }

        if(methodName.equals("signup")) {
            SignupReqDto signupReqDto = null;

            for(Object arg : args) {
                if(arg.getClass() == SignupReqDto.class) {
                    signupReqDto = (SignupReqDto) arg;
                }
            }

            if(userMapper.findUserByUsername(signupReqDto.getUsername()) != null) {
                ObjectError objectError = new FieldError("username", "username", "이미 존재하는 사용자 이름입니다.");
                bindingResult.addError(objectError);
            }

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
