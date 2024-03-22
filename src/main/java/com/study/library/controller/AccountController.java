package com.study.library.controller;

import com.study.library.aop.annotation.ValidAspect;
import com.study.library.dto.EditPasswordReqDto;
import com.study.library.sercurity.PrincipalUser;
import com.study.library.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal() {
        // System.out.println("test");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal(); //principalUser 는 object 타입이므로 다운캐스팅
        return ResponseEntity.ok(principalUser);
    }

    @ValidAspect
    @PutMapping("/password")
    public ResponseEntity<?> editPassword(
            @Valid @RequestBody EditPasswordReqDto editPasswordReqDto, BindingResult bindingResult
    ) {
        accountService.editPassword(editPasswordReqDto);
        return ResponseEntity.ok(true);
    }
}
