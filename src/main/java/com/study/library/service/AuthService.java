package com.study.library.service;

import com.study.library.dto.OAuth2SignupReqDto;
import com.study.library.dto.SigninReqDto;
import com.study.library.dto.SignupReqDto;
import com.study.library.entity.User;
import com.study.library.exception.SaveException;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    public boolean isDuplicatedByUsername(String username) {
        return userMapper.findUserByUsername(username) != null; // null 이 아니라면 값 중복
    }

    @Transactional(rollbackFor = Exception.class) // 예외 하나라도 생기면 무조건 롤백
    public void signup(SignupReqDto signupReqDto) {
        int successCount = 0;

        User user = signupReqDto.toEntity(passwordEncoder);

        successCount += userMapper.saveUser(user); // Dto를 Entity로 변환 후 넣어주기
        successCount += userMapper.saveRole(user.getUserId(), 1);

        if(successCount < 2) {
            throw new SaveException();
        }
    }
    @Transactional(rollbackFor = Exception.class) // 예외 하나라도 생기면 무조건 롤백
    public void oAuthSignup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        int successCount = 0;

        User user = oAuth2SignupReqDto.toEntity(passwordEncoder);

        successCount += userMapper.saveUser(user); //
        successCount += userMapper.saveRole(user.getUserId(), 1);
        successCount += userMapper.saveOAuth2(oAuth2SignupReqDto.toOAuth2Entity(user.getUserId()));

        if(successCount < 3) {
            throw new SaveException();
        }
    }

    public String signin(SigninReqDto signinReqDto) {
        User user = userMapper.findUserByUsername(signinReqDto.getUsername());

        if(user == null) {
            throw new UsernameNotFoundException/*아이디 불일치*/("사용자 정보를 확인할 수 없습니다.");
        }

        if (!passwordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException/*비밀번호 불일치*/("사용자 정보를 확인할 수 없습니다.");
        }


        return jwtProvider.generateToken(user);
    }

}
