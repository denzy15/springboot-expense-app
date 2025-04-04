package com.example.expense.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final PublicKeyProvider publicKeyProvider;
    private final RestTemplate restTemplate;

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


}
