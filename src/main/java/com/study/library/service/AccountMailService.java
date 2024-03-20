package com.study.library.service;

import com.study.library.entity.RoleRegister;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import com.study.library.sercurity.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class AccountMailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserMapper userMapper;

    @Value("${spring.mail.address}")
    protected String fromMailAddress;
    @Value("${server.deploy-address}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

    public boolean sendAuthMail() {
        boolean result = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal(); // Authentication이 PrincipalUser을 상속 받으므로 다운캐스팅
        int userId = principalUser.getUserId();
        String toMailAddress = principalUser.getEmail();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage(); // 객체 생성, mimeMessage 속에 메세지 넣어서 javaMailSender로 전송

        try { // MimeMessageHelper -> mimeMessage에 메세지에 들어갈 내용을 들어가게 도와줌
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8"); // 텍스트만 보낼때 false, 파일 혹은 이미지 일때 true
            helper.setSubject("도서관리 시스템 사용자 메일 인증");
            helper.setFrom(fromMailAddress);
            helper.setTo(toMailAddress);

            String authMailToken = jwtProvider.generateAuthMailToken(userId, toMailAddress);

            StringBuilder mailContent = new StringBuilder();
            mailContent.append("<div>");
            mailContent.append("<h1>계정 활성화 절차</h1>");
            mailContent.append("<h3>해당 계정을 인증하려면 아래의 버튼을 눌러주세요.</h3>");
            mailContent.append( // http://localhost:8080/mail/authenticate?authToken=JWT토큰
                    "<a href=\"http://" + serverAddress + ":" + serverPort +"/mail/authenticate?authToken=" + authMailToken +"\" " +
                    "style=\"border: 1px solid #dbdbdb; " +
                    "padding: 10px 20px; " +
                    "text-decoration: none; " +
                    "background-color: white; " +
                    "color: #222222;\">인증하기</a>"
            );
            mailContent.append("</div>");

            mimeMessage.setText(mailContent.toString(), "utf-8", "html");

            javaMailSender.send(mimeMessage); // 메일 전송
            result = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Object> authenticate(String token) {
        Claims claims = null;
        Map<String, Object> resultMap = null;

        // ExpiredJwtException => 토큰 유효 만료
        // MalformedJwtException => 위조, 변조
        // SignatureException => 형식, 길이 오류
        // IllegalArgumentException => null 또는 빈값
        try {
            claims = jwtProvider.getClaims(token);
            int userId = Integer.parseInt(claims.get("userId").toString());
            RoleRegister roleRegister = userMapper.findRoleRegisterByUserIdAndRoleId(userId, 2);
            if(roleRegister != null) {
                resultMap = Map.of("status", true, "message", "인증 완료된 메일입니다.");
            } else {
                userMapper.saveRole(userId, 2);
                resultMap = Map.of("status", true, "message", "인증 완료");
            }
        } catch (ExpiredJwtException e) {
            resultMap = Map.of("status", false, "message", "인증시간이 만료되었습니다.\n다시 인증해주세요.");
        } catch (JwtException e) {
            resultMap = Map.of("status", false, "message", "잘못된 접근입니다.\n다시 인증해주세요.");
        }

        return resultMap;
    }
}
