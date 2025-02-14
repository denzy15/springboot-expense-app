package com.example.expense.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final PublicKeyProvider publicKeyProvider;
    private final RestTemplate restTemplate;

    @Value("${auth.service.public-refresh-token-url}")
    private String refreshUrl;

    /**
     * Метод для извлечения информации из JWT
     */
    public Claims parseToken(String token) {
        try {
            PublicKey publicKey = publicKeyProvider.getAccessTokenKey(); // Получаем публичный ключ

            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token); // Декодируем токен

            return claimsJws.getBody(); // Возвращаем payload токена

        } catch (SignatureException e) {
            throw new RuntimeException("Невалидный токен", e);
        }
    }

    /**
     * Метод для обновления accessToken по refreshToken
     */
    public String refreshAccessToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, "jwt=" + refreshToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(refreshUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("accessToken");
            }
        } catch (Exception e) {
            logger.error("Ошибка обновления токена: {}", e.getMessage());
        }
        return null;
    }
}
