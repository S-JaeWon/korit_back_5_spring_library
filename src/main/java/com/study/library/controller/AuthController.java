package com.study.library.controller;

import com.study.library.aop.annotation.ParamsPrintAspect;
import com.study.library.dto.SignupReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // /auth를 공통주소로 정하면 아래의 모든 주소는 앞에 /auth가 붙음
public class AuthController {

    @ParamsPrintAspect
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupReqDto signupReqDto
    ) {
        return ResponseEntity.ok(null);
    }

}
