package com.example.expense.config;

import com.example.expense.service.UserService;
import com.example.expense.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                                .requestMatchers("auth/*").permitAll()
                                .anyRequest().authenticated()

                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                //                .cors(withDefaults())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Component
    @RequiredArgsConstructor
    @Slf4j
    public static class JwtRequestFilter extends OncePerRequestFilter {
        private final JwtTokenUtils jwtTokenUtils;


        @SneakyThrows
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

//                        String authHeader = request.getHeader("Authorization");
//                        String email = null;
//                        String jwt = null;
//
//                        if (request.getServletPath().startsWith("/auth")) {
//                            filterChain.doFilter(request, response);
//                            return;
//                        }
//
//                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                            jwt = authHeader.substring(7);
//                            try {
//                                email = jwtTokenUtils.getUserEmail(jwt);
//                            } catch (ExpiredJwtException e) {
////                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt token has expired");
//                                handleJwtException(response, "Jwt expired");
//                                return;
//                            } catch (SignatureException e) {
//                                handleJwtException(response, "Wrong signature");
////                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt token has wrong signature");
//                                return;
//                            }
//                        } else {
//                                handleJwtException(response, "Unauthorized su4ka");
////                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized su4ka");
//                        }
//
//                        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                            List<GrantedAuthority> authorities = Collections.singletonList(new
//                            SimpleGrantedAuthority("USER"));
//
//                            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken
//                            (email, null,
//                                    authorities);
//                            SecurityContextHolder.getContext().setAuthentication(token);
//                        }
//
//                        filterChain.doFilter(request, response);
            String authHeader = request.getHeader("Authorization");
            String email = null;
            String jwt = null;
            System.out.println(request.getServletPath());

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                try {
                    email = jwtTokenUtils.getUserEmail(jwt);
                } catch (ExpiredJwtException e) {
                    log.debug("Jwt expired");
                    response.setHeader("custom-error", "Jwt Expired");

                } catch (SignatureException e) {
                    response.setHeader("custom-error", "Wrong data provided");

                    log.debug("Wrong sign");
                }


            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            filterChain.doFilter(request, response);

        }


                private void handleJwtException(HttpServletResponse response, String errorMessage) throws
                IOException {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(errorMessage);
                    response.getWriter().flush();
                    response.getWriter().close();


                }

    }
}

