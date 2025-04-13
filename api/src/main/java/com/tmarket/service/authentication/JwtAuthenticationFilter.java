package com.tmarket.service.authentication;

import com.tmarket.exception.JwtExceptions;
import com.tmarket.model.member.LoginDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("X-Refresh-Token"); // X- prefix : HTTP 커스텀 헤더라는 의미(관례)

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            try {
                // 1. 토큰에서 사용자 이메일(또는 ID) 추출
                String email = authenticationService.validateTokenAndGetUserId(accessToken);

                // 2. Authentication 객체 생성 (권한 없으면 null로 처리)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, null); // authorities 생략 가능

                // 3. SecurityContext에 등록 (SecurityContextHolder: 현재 인증된 사용자의 정보가 쓰레드 로컬(ThreadLocal)에 보관되는 컨텍스트 저장소)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
                return;
            } catch (JwtExceptions.TokenNeedsReissueException e) {
                try {
                    LoginDTO.LoginResponse reissuedTokens = authenticationService.reAuthenticateUser(accessToken, refreshToken);
                    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokens.getAccessToken());
                    response.setHeader("X-New-Refresh-Token", reissuedTokens.getRefreshToken());
                    chain.doFilter(request, response);
                    return;
                } catch (Exception ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "리프래시 토큰 만료 또는 재인증 실패");
                    return;
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 검증 실패");
                return;
            }
        }

        chain.doFilter(request, response); // 인증이 필요 없는 요청
    }
}
