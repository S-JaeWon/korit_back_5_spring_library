package com.study.library.sercurity.handler;

import com.study.library.entity.User;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${client.deploy-address}")
    private String clientAddress;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        String name = authentication.getName();
        User user = userMapper.findUserByOAuth2name(name); // 해당 이름이 있는 유저 확인 (없으면 회원가입)

        if(user == null) {// OAuth2 로그인을 통해 회원가입이 되어있지 않은 상태, OAuth2 동기화
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String providerName = oAuth2User.getAttribute("provider").toString();

            // 8080서버에서 3000서버로 넘어옴
            response.sendRedirect("http://" + clientAddress + "/auth/oauth2?name=" + name + "&provider=" + providerName);
            return;
        }

        // OAuth2 로그인을 통해 회원가입을 진행한 기록이 있는지
        String accessToken = jwtProvider.generateToken(user);
        response.sendRedirect("http://" + clientAddress + "/auth/oauth2/signin?accessToken=" + accessToken);

    }
}
