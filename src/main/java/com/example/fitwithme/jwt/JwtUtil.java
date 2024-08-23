package com.example.fitwithme.jwt;

import com.example.fitwithme.presentation.dto.response.UserResponse;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessTokenValidity}")
    private long accessTokenValidity;

    @Value("${jwt.refreshTokenValidity}")
    private long refreshTokenValidity;

    @Getter
    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public UserResponse.tokenInfo generateTokens(String userId) {
        String accessToken = createAccessToken(userId);
        String refreshToken = createRefreshToken(userId);

        saveTokenToRedis(userId, accessToken, refreshToken);

        UserResponse.tokenInfo tokenInfo = UserResponse.tokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return tokenInfo;
    }

    private String createAccessToken(String userId) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("type", "Access");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("AccessToken")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String createRefreshToken(String userId) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("type", "Refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("RefreshToken")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String refreshAccessToken(String refreshToken) {
        if (validateToken(refreshToken)) {
            Claims claims = getClaimsFromToken(refreshToken);
            String userId = claims.get("userId", String.class);

            String redisRefreshToken = redisTemplate.opsForValue().get("REFRESH_TOKEN:" + userId);
            if (redisRefreshToken != null && redisRefreshToken.equals(refreshToken)) {
                return createAccessToken(userId);
            }
        }
        return null;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", String.class);
    }

    private void saveTokenToRedis(String userId, String accessToken, String refreshToken) {
        redisTemplate.opsForValue().set("ACCESS_TOKEN:" + userId, accessToken, accessTokenValidity, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set("REFRESH_TOKEN:" + userId, refreshToken, refreshTokenValidity, TimeUnit.MILLISECONDS);
    }

}
