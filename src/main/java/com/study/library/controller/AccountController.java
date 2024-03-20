package com.study.library.controller;

import com.study.library.sercurity.PrincipalUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal() {
        // System.out.println("test");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal(); //principalUser 는 object 타입이므로 다운캐스팅
        return ResponseEntity.ok(principalUser);
    }
}
