package com.study.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration // @Bean 수동 등록 할 때
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // 서버 사이드 렌더링일때 사용 되므로 사용X
        http.authorizeRequests() // 요청이 들어올때 다음과 같은걸로 인증. http 객체 안에 빌더 패턴 사용
                .antMatchers("/server/**", "/auth/**") // 다음과 같은 서버
                .permitAll() // 무조건 승인
                .anyRequest() // 나머지 모든 요청
                .authenticated(); // 인증 필요
        // 'server와 auth로 시작하는 서버의 요청은 무조건 승인, 이외는 인증 필요', 순서를 바꾸면 다른 뜻이 됨

    }
}
