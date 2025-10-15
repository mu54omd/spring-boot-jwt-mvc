package com.musashi.spring_boot_jwt.service;

import com.musashi.spring_boot_jwt.model.RefreshToken;
import com.musashi.spring_boot_jwt.model.TokenPair;
import com.musashi.spring_boot_jwt.model.User;
import com.musashi.spring_boot_jwt.security.HashEncoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {

    private UserService userService;
    private JwtService jwtService;
    private HashEncoder hashEncoder;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(UserService userService, JwtService jwtService, HashEncoder hashEncoder, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.hashEncoder = hashEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(String email, String password){
        User user = userService.findByEmail(email);
        if(user != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exist!");
        }
        userService.save(new User(email, hashEncoder.encode(password)));
    }

    public TokenPair login(String email, String password, HttpServletResponse response, HttpServletRequest request) {
        User user = userService.findByEmail(email);
        if(user == null){
            throw new BadCredentialsException("Invalid credentials!");
        }
        if(!hashEncoder.matches(password, user.getHashedPassword())){
            throw new BadCredentialsException("Invalid credentials!");
        }
        String newAccessToken;
        String newRefreshToken;
        String oldAccessToken = null;
        String oldRefreshToken = null;

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_ACCESS_TOKEN".equals(cookie.getName())) {
                    if(jwtService.getUserIdFromToken(cookie.getValue()).equals(user.getId().toHexString())) {
                        oldAccessToken = cookie.getValue();
                    }
                }
                if ("JWT_REFRESH_TOKEN".equals(cookie.getName())) {
                    if (jwtService.getUserIdFromToken(cookie.getValue()).equals(user.getId().toHexString())) {
                        oldRefreshToken = cookie.getValue();
                    }
                }
            }
        }

        if(oldAccessToken != null && jwtService.validateAccessToken(oldAccessToken)){
            return new TokenPair(oldAccessToken, oldRefreshToken);
        }
        if(oldRefreshToken != null){
            TokenPair tokenPair =  refresh(oldRefreshToken);
            newAccessToken = tokenPair.getAccessToken();
            newRefreshToken = tokenPair.getRefreshToken();
            storeCookies(newAccessToken, newRefreshToken, response);
            return tokenPair;
        }
        newAccessToken = jwtService.generateAccessToken(user.getId().toHexString());
        newRefreshToken = jwtService.generateRefreshToken(user.getId().toHexString());

        storeCookies(newAccessToken, newRefreshToken, response);
        storeRefreshToken(user.getId(), newRefreshToken);
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token!");
        }

        String userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.findById(new ObjectId(userId));
        if(user == null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token!");
        }
        String hashedRefreshToken = hashedToken(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenService.findByUserIdAndHashedToken(user.getId(), hashedRefreshToken);
        if(storedRefreshToken == null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Refresh token not recognized! (Maybe used or expired?!)");
        }
        refreshTokenService.deleteByUserIdAndHashedToken(user.getId(), hashedRefreshToken);
        String newAccessToken = jwtService.generateAccessToken(userId);
        String newRefreshToken = jwtService.generateRefreshToken(userId);

        storeRefreshToken(user.getId(), newRefreshToken);
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_ACCESS_TOKEN".equals(cookie.getName()) || "JWT_REFRESH_TOKEN".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }
            }
        }
    }

    private void storeRefreshToken(ObjectId userId, String rawRefreshToken) {
        String hashedToken = hashedToken(rawRefreshToken);
        long expiresMs = jwtService.getRefreshTokenValidityMs();
        Instant expiresAt = Instant.now().plusMillis(expiresMs);
        refreshTokenService.save(new RefreshToken(userId, expiresAt, hashedToken));
    }

    private String hashedToken(String rawToken) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        }catch (Exception ignored){}
        assert digest != null;
        byte[] hashBytes = digest.digest(rawToken.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private void storeCookies(String newAccessToken, String newRefreshToken, HttpServletResponse response){
        Cookie jwtAccessTokenCookie = cookieConfiguration("JWT_ACCESS_TOKEN", newAccessToken, jwtService.getAccessTokenValidityMs());
        Cookie jwtRefreshTokenCookie = cookieConfiguration("JWT_REFRESH_TOKEN", newRefreshToken, jwtService.getRefreshTokenValidityMs());
        response.addCookie(jwtAccessTokenCookie);
        response.addCookie(jwtRefreshTokenCookie);
    }

    private Cookie cookieConfiguration(String cookieName, String cookieValue, long expiryMs){
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) expiryMs / 1000);
        return cookie;
    }
}
