package com.study.library.sercurity.filter;

import com.study.library.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends GenericFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Boolean isPermitAll = (Boolean)request.getAttribute("isPermitAll");

        if(!isPermitAll) { // 인증이 필요한 응답 일 때 토큰 가져옴, 토큰이 유효하지 않거나 만료 될때 claims = null -> 401 오류
            String accessToken = request.getHeader("Authorization"); // 리액트 서버 헤더에서 Token 가져옴
            String removeBearerToken = jwtProvider.removeBearer(accessToken); // 앞에 Bearer 제거 하고 토큰을 가져옴
            Claims claims = null;

            try {
                claims = jwtProvider.getClaims(removeBearerToken);// claims 에 removeBearerToken 넣기
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패, error: 401
                return;
            }

            Authentication authentication = jwtProvider.getAuthentication(claims);

            if(authentication == null) { // 인증 실패시
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authentication); // authentication 안으로 데이터 값 들어가면 인증, 아니면 403 인증오류

        }
        // 전처리
        filterChain.doFilter(request, response); // 다음 필터로
        // 후처리

    }
}
