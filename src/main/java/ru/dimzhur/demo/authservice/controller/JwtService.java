package ru.dimzhur.demo.authservice.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с токенами доступа
 */
@Service
public class JwtService {

    /**
     * Ключ для подписи токена
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Продолжительность действия токена доступа
     */
     static final public long ACCESS_LIVE = 1000 * 60 * 60 * 24;
    /**
     * Продолжительность действия токена обновления
     */
    static final public long REFRESH_LIVE = 1000L * 60 * 60 * 24 * 30;

    /**
     * Извлечение логина пользователя
     *
     * @param token токен доступа
     * @return ID пользователя
     */
    public String getUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлечение ID пользователя
     *
     * @param token токен доступа
     * @return ID пользователя
     */
    public String getTokenId(String token) {
        return extractClaim(token, (Claims claims) -> claims.get("tokenId", String.class));
    }


    /**
     * Извлечение данных из токена
     *
     * @param token          токен
     * @param claimsResolver функция извлечения
     * @param <T>            тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен доступа
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Ключ для шифрования
     * @return ключ из секретной фразы
     */
    private SecretKey getSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @param refresh     флаг, указывающий, что необходимо сгенерировать токен обновления
     * @param creationTime время создания
     * @param tokenId ID токена
     * @return токен
     */
    public String generateToken(UserDetails userDetails, boolean refresh, String tokenId, long creationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenId", tokenId);
        if (refresh) {
            claims.put("live", REFRESH_LIVE);
        } else {
            claims.put("live", ACCESS_LIVE);
        }
        return generateToken(claims, userDetails, refresh, creationTime);
    }

    /**
     * Генерация токена
     *
     * @param claims      дополнительные данные
     * @param userDetails данные пользователя
     * @param refresh     флаг, указывающий, что необходимо сгенерировать токен обновления
     * @param creationTime время создания
     * @return токен
     */
    private String generateToken(Map<String, Object> claims, UserDetails userDetails, boolean refresh, long creationTime) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(creationTime))
                .expiration(new Date(creationTime + (refresh ? REFRESH_LIVE : ACCESS_LIVE)))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен доступа
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Проверка срока действия токена
     *
     * @param token токен
     * @return флаг истечения срока действия токена
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Проверка токена на валидность
     * @param token токен
     * @param userDetails данные пользователя
     * @return флаг валидности
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUserId(token);
        return (userDetails.getUsername().equals(username) && !isTokenExpired(token));
    }
}
