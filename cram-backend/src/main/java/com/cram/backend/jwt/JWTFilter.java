package com.cram.backend.jwt;

import com.cram.backend.user.dto.CustomOAuth2User;
import com.cram.backend.user.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter {

    private  final com.cram.backend.jwt.JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 예외 경로 처리
        if (requestUri.matches("^/login(?:/.*)?$") ||
                requestUri.matches("^/oauth2(?:/.*)?$") ||
                requestUri.matches("^/h2-console(?:/.*)?$") ||
                requestUri.matches("^/favicon\\.ico$")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 Bearer 토큰 꺼내기
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("token null or invalid header format");
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 접두사 제거 (앞 7글자 잘라냄)
        String accessToken = authorizationHeader.substring(7);

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 현재 username, role 가져오기
        Long userId = jwtUtil.getUserId(accessToken);
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setUsername(username);
        userDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}