package com.study.library.dto;

import com.study.library.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class SignupReqDto {

    // 정규식 https://adjh54.tistory.com/104#1)%20%EC%A0%95%EA%B7%9C%ED%91%9C%ED%98%84%EC%8B%9D(Regular%20Expression)-1
    @Pattern(regexp = "^[a-z]{1}[a-z0-9]{2,10}+$", message = "영문 숫자 조합 3~10 자리이어야 합니다.")
    private String username;
    @Pattern(regexp = "^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{7,128}+$", message = "대소문자와 숫자, 특수문자 조합으로 8~128 자리이어야 합니다.")
    private String password;
    @Pattern(regexp = "^[ㄱ-ㅎ|가-힣]{1,}$", message = "최소 두 글자의 한글로 표시해주세요.")
    private String name;
    @Email(regexp = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]+$")
    private String email;

    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .email(email)
                .build();
    }
}
