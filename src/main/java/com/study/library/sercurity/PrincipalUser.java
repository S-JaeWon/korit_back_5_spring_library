package com.study.library.sercurity;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Data
public class PrincipalUser implements UserDetails {
    private int userId;
    private String username;
    private String name;
    private String email;
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    // 계정 사용기간 만료 ex)휴면계정
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 ex)비밀번호5회 틀릴시 ~~
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 사용기간 만료
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 비활성화 ex)계정블락
    @Override
    public boolean isEnabled() {
        return true;
    }
}
