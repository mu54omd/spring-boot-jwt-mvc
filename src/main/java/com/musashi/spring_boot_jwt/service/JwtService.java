package com.musashi.spring_boot_jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;


    private SecretKey secretKey;

    private long accessTokenValidityMs = 1L * 60L * 1000L;
    private long refreshTokenValidityMs = 5L * 24L * 60L * 60L * 1000L;

    public long getAccessTokenValidityMs() {
        return accessTokenValidityMs;
    }

    public long getRefreshTokenValidityMs() {
        return refreshTokenValidityMs;
    }

    @PostConstruct
    private void init(){
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }


    public String generateAccessToken(String userId){
        return generateToken(userId, "access", accessTokenValidityMs);
    }

    public String generateRefreshToken(String userId){
        return generateToken(userId, "refresh", refreshTokenValidityMs);
    }

    public boolean validateAccessToken(String token){
        Claims claims = parseAllClaims(token);
        if(claims == null){
            return false;
        }
        String tokenType = claims.get("type").toString();
        if(tokenType == null){
            return false;
        }
        return tokenType.equals("access");
    }

    public boolean validateRefreshToken(String token){
        Claims claims = parseAllClaims(token);
        if(claims == null){
            return false;
        }
        String tokenType = claims.get("type").toString();
        if(tokenType == null){
            return false;
        }
        return tokenType.equals("refresh");
    }

    public String getUserIdFromToken(String token){
        Claims claims = parseAllClaims(token);
        if(claims == null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid token.");
        }
        return claims.getSubject();
    }

    private String generateToken(String userId, String type, long expiry){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiry);
        return Jwts.builder()
                .subject(userId)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseAllClaims(String token){
        String rawToken = token;
        if(token.startsWith("Bearer ")) {
            rawToken = token.replaceFirst("Bearer ", "");
        }
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(rawToken)
                    .getPayload();
        }catch (Exception ignored){

        }
        return claims;
    }

}
