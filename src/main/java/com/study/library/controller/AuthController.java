package com.study.library.controller;

import com.study.library.aop.annotation.ParamsPrintAspect;
import com.study.library.aop.annotation.ValidAspect;
import com.study.library.dto.SignupReqDto;
import com.study.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth") // /auth를 공통주소로 정하면 아래의 모든 주소는 앞에 /auth가 붙음
public class AuthController {

    @Autowired
    private AuthService authService;

    @ValidAspect
    @PostMapping("/signup")
    public ResponseEntity<?> signup( // ReqDto를 Validation 체크 후 결과를 bindingResult에 담음
           @Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult
    ) {
        if(authService.isDuplicatedByUsername(signupReqDto.getUsername())) { // username 중복체크
            ObjectError objectError = new FieldError("username", "username", "이미 존재하는 사용자이름 입니다.");
            bindingResult.addError(objectError);
        }

//          Aop로 빼서 쓰기
//        if (bindingResult.hasErrors()) { // error가 있다면, 그 error들을 list로 가져옴
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            Map<String, String> errorMap = new HashMap<>();
//            for(FieldError fieldError : fieldErrors) {
//                String fieldName = fieldError.getField(); // DTO 변수명
//                String message = fieldError.getDefaultMessage(); // 메세지 내용
//                errorMap.put(fieldName, message);
//            }
//            return ResponseEntity.badRequest().body(errorMap);
//        }

        authService.signup(signupReqDto);
        return ResponseEntity.created(null).body(true);
    }

}
