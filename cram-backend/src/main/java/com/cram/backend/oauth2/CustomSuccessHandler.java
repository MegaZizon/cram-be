package com.cram.backend.oauth2;

import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.user.entity.RefreshEntity;
import com.cram.backend.user.repository.RefreshRepository;
import com.cram.backend.user.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUserDetails.getUserId();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // access, refresh 각각 생성
        String accessToken = jwtUtil.createJwt("access", userId, username, role, 600000L); // 10분
        String refreshToken = jwtUtil.createJwt("refresh", userId, username, role, 86400000L); // 1일

        // 기존 토큰 삭제
        refreshRepository.deleteByRefresh(refreshToken);

        // Refresh Token 저장
        addRefreshEntity(userId, username, refreshToken, 86400000L);

        // Access Token은 헤더로 전달 (프론트에서 저장하도록)
        response.setHeader("access", accessToken);

        // Refresh Token은 HttpOnly 쿠키로 전달
        response.addCookie(createCookie("refresh", refreshToken));

        // 성공 후 리다이렉션
        response.sendRedirect("http://localhost:8080/");
    }

    private void addRefreshEntity(Long userId, String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserId(userId);
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키 시간
        //cookie.setSecure(true); // https 통신만 가능하도록
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
