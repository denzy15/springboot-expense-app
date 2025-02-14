package com.example.expense.config;

import com.example.expense.DTO.UserPrincipal;
import com.example.expense.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        if (accessToken != null) {
            try {
                Claims claims = jwtService.parseToken(accessToken);
                authenticateUser(claims);
            } catch (Exception e) {
                logger.warn("Access token недействителен. Пробуем обновить...");

                String refreshToken = getRefreshTokenFromCookies(request);
                if (refreshToken != null) {
                    String newAccessToken = jwtService.refreshAccessToken(refreshToken);

                    if (newAccessToken != null) {
                        logger.info("Access token успешно обновлен.");
                        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

                        Claims claims = jwtService.parseToken(newAccessToken);
                        authenticateUser(claims);
                    } else {
                        logger.error("Refresh token недействителен. Пользователь должен войти заново.");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        return;
                    }
                } else {
                    logger.error("Refresh token отсутствует. Unauthorized.");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(Claims claims) {
        Long userId = claims.get("id", Long.class);
        String email = claims.get("email", String.class);
        String username = claims.get("username", String.class);

        UserPrincipal principal = new UserPrincipal(userId, email, username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
