package com.example.expense.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*"));
//        configuration.setAllowedMethods(List.of("*"));
//        configuration.setAllowedHeaders(List.of("*"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        daoAuthenticationProvider.setUserDetailsService(userService);
//        return daoAuthenticationProvider;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }


//    @Component
//    @RequiredArgsConstructor
//    @Slf4j
//    public static class JwtRequestFilter extends OncePerRequestFilter {
//        private final JwtTokenUtils jwtTokenUtils;
//
//
//        @SneakyThrows
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                        FilterChain filterChain) throws ServletException, IOException {
//
//            String authHeader = request.getHeader("Authorization");
//            String email = null;
//            String jwt = null;
//
//            if (request.getServletPath().startsWith("/auth")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                jwt = authHeader.substring(7);
//                try {
//                    email = jwtTokenUtils.getUserEmail(jwt);
//                } catch (ExpiredJwtException e) {
//                    handleJwtException(response, "Jwt expired");
//                    return;
//
//                } catch (SignatureException e) {
//                    handleJwtException(response, "Wrong signature");
//                    return;
//                }
//            } else {
//                handleJwtException(response, "Unauthorized");
//                return;
//            }
//
//            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                List<GrantedAuthority> authorities = Collections.singletonList(new
//                        SimpleGrantedAuthority("USER"));
//
//                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken
//                        (email, null,
//                                authorities);
//                SecurityContextHolder.getContext().setAuthentication(token);
//            }
//
//            filterChain.doFilter(request, response);
//
//        }
//
//
//        private void handleJwtException(HttpServletResponse response, String errorMessage) throws
//                IOException {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            response.getWriter().write(errorMessage);
//            response.getWriter().flush();
//            response.getWriter().close();
//
//
//        }
//
//    }
}

