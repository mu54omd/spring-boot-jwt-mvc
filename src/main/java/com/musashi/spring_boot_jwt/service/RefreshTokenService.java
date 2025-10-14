package com.musashi.spring_boot_jwt.service;

import com.musashi.spring_boot_jwt.model.RefreshToken;
import org.bson.types.ObjectId;

public interface RefreshTokenService {
    void save(RefreshToken refreshToken);
    RefreshToken findByUserIdAndHashedToken(ObjectId userId, String hashedToken);
    void deleteByUserIdAndHashedToken(ObjectId userId, String hashedToken);
}
