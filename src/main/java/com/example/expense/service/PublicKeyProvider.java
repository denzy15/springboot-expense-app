package com.example.expense.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.util.Base64;

@Service
public class PublicKeyProvider {
    private static final Logger logger = LoggerFactory.getLogger(PublicKeyProvider.class);

    private final RestTemplate restTemplate;

    @Getter
    private PublicKey accessTokenKey;
    @Getter
    private PublicKey refreshTokenKey;

    @Value("${auth.service.public-access-key-url}")
    private String accessKeyUrl;

    @Value("${auth.service.public-refresh-key-url}")
    private String refreshKeyUrl;

    public PublicKeyProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** Загружаем ключи при старте */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        updatePublicKeys();
    }

    /** Авто-обновление ключей каждые 6 часов */
    @Scheduled(fixedRate = 21600000) // 6 часов
    public void scheduledKeyUpdate() {
        updatePublicKeys();
    }

    /** Обновляем оба ключа */
    public void updatePublicKeys() {
        this.accessTokenKey = fetchPublicKey(accessKeyUrl);
        this.refreshTokenKey = fetchPublicKey(refreshKeyUrl);
    }

    /** Метод для получения ключа по URL */
    private PublicKey fetchPublicKey(String url) {
        try {
            logger.info("Fetching public key from: {}", url);
            String keyPem = restTemplate.getForObject(url, String.class);

            if (keyPem == null || keyPem.isEmpty()) {
                logger.warn("Received empty public key from {}", url);
                return null;
            }

            return convertPemToPublicKey(keyPem);
        } catch (Exception e) {
            logger.error("Error fetching public key from {}: {}", url, e.getMessage());
            return null;
        }
    }

    /** Преобразование PEM в PublicKey */
    private PublicKey convertPemToPublicKey(String pem) {
        try {
            logger.info("Raw Public Key before processing:\n{}", pem);

            // Очистка от заголовков, переводов строк и пробелов
            String cleanedKey = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "") // Удаляем все пробельные символы (включая \r, \n, \t, пробелы)
                    .replace("\"", ""); // Убираем кавычки, если они есть

            logger.info("Cleaned Public Key:\n{}", cleanedKey);

            byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            logger.error("Invalid public key format: {}", e.getMessage());
            return null;
        }
    }



}
