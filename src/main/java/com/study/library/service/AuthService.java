package com.study.library.service;

import com.study.library.dto.SignupReqDto;
import com.study.library.entity.User;
import com.study.library.exception.SaveException;
import com.study.library.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean isDuplicatedByUsername(String username) {
        return userMapper.findUserByUsername(username) != null; // null 이 아니라면 값 중복
    }

    @Transactional(rollbackFor = Exception.class) // 예외 하나라도 생기면 무조건 롤백
    public void signup(SignupReqDto signupReqDto) {
        int successCount = 0;

        User user = signupReqDto.toEntity(passwordEncoder);

        successCount += userMapper.saveUser(user); // Dto를 Entity로 변환 후 넣어주기
        successCount += userMapper.saveRole(user.getUserId());

        if(successCount < 2) {
            throw new SaveException();
        }
    }

}
