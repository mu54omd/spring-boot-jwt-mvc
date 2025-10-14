package com.musashi.spring_boot_jwt.service;

import com.musashi.spring_boot_jwt.model.RefreshToken;
import com.musashi.spring_boot_jwt.repository.RefreshTokenRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findByUserIdAndHashedToken(ObjectId userId, String hashedToken) {
        return refreshTokenRepository.findByUserIdAndHashedToken(userId, hashedToken);
    }

    @Override
    public void deleteByUserIdAndHashedToken(ObjectId userId, String hashedToken) {
        refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashedToken);
    }
}
