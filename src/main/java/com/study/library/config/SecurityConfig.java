package com.study.library.config;

import com.study.library.sercurity.exception.AuthEntryPoint;
import com.study.library.sercurity.filter.JwtAuthenticationFilter;
import com.study.library.sercurity.filter.PermitAllFilter;
import com.study.library.sercurity.handler.OAuth2SuccessHandler;
import com.study.library.service.OAuth2PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@Configuration // @Bean 수동 등록 할 때
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PermitAllFilter permitAllFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Autowired
    private OAuth2PrincipalService oAuth2PrincipalService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors(); // crossOrigin 필터
        http.csrf().disable(); // 서버 사이드 렌더링일때 사용 되므로 사용X
        http.authorizeRequests() // 요청이 들어올때 다음과 같은걸로 인증. http 객체 안에 빌더 패턴 사용
                .antMatchers("/server/**", "/auth/**") // 다음과 같은 서버
                .permitAll() // 무조건 승인
                .antMatchers("/mail/authenticate")
                .permitAll()
                .anyRequest() // 나머지 모든 요청
                .authenticated() // 인증 필요
                .and()
                .addFilterAfter(permitAllFilter, LogoutFilter.class) // 1)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 2) (1)(2)filter 들을 거쳐야 antMatchers 부터 실행
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and()
                .oauth2Login()
                .successHandler(oAuth2SuccessHandler) // 밑에서 검사 끝나면 여기로 보냄
                // OAuth2로그인 토큰 검사
                .userInfoEndpoint()
                .userService(oAuth2PrincipalService);

        // 'server와 auth로 시작하는 서버의 요청은 무조건 승인, 이외는 인증 필요', 순서를 바꾸면 다른 뜻이 됨

    }
}
