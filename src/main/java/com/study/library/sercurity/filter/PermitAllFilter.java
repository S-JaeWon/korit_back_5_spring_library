package com.study.library.sercurity.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class PermitAllFilter extends GenericFilter {

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        List<String> antMatchers = List.of(
                        "/login",
                        "/error",
                        "/server",
                        "/auth",
                        "/oauth2",
                        "/mail/authenticate"
        );

        String uri = request.getRequestURI();
        request.setAttribute("isPermitAll", false);
        for(String antMatcher : antMatchers) { // "/error", "/server", "/auth" 하나라도 포함 하면 인증 필요x (토큰 필요 없음)
            if(uri./*contains*/startsWith(antMatcher)) {
                request.setAttribute("isPermitAll", true);
            }
        }
        filterChain.doFilter(request, response);
    }
}
