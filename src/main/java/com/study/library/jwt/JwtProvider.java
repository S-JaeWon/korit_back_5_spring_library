package com.study.library.jwt;

import com.study.library.entity.User;
import com.study.library.repository.UserMapper;
import com.study.library.sercurity.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider { // Token을 만들어서 return

    private final Key key;

    private UserMapper userMapper;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Autowired UserMapper userMapper
    ) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userMapper = userMapper;
    }

    public String generateToken(User user) {

        int userId = user.getUserId();
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        Date expireDate = new Date(new Date().getTime() + (1000 * 60 * 60* 24));

        String accessToken = Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("authorities", authorities)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    public String removeBearer(String token) {
        if(!StringUtils.hasText(token)) {
            return null;
        }
        return token.substring("bearer ".length()/*인덱스 7*/);
    }

    public Claims getClaims(String token) {
        Claims claims = null;

        claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) // 토큰을 Claim 으로 변환
                .getBody();

        return claims;
    }

    public Authentication getAuthentication(Claims claims) {
        String username = claims.get("username").toString(); // claims에서 유저네임 갖고 오기
        User user = userMapper.findUserByUsername(username); // 유저네임으로 Mapper에서 데이터 갖고 오기
        if(user == null) { // 토큰은 유효 하지만 DB에 해당 데이터값이 삭제되었을때 (로그인은 됐지만 유저 정보가 없을때) null로 return
            return null;
        }
        PrincipalUser principalUser = user.toPrincipalUser(); // principalUser로 Authortication 만들기
        return new UsernamePasswordAuthenticationToken(principalUser, principalUser.getPassword(), principalUser.getAuthorities());
    }

    public String generateAuthMailToken(int userId, String toMailAddress) { // address 값과 만료시간을 암호화한 정보가 담긴 토큰
        Date expireDate = new Date(new Date().getTime() + (1000 * 60 * 5));
        return Jwts.builder()
                .claim("userId", userId)
                .claim("toMailAddress", toMailAddress)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
