package com.musashi.spring_boot_jwt.repository;

import com.musashi.spring_boot_jwt.model.RefreshToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, ObjectId> {
    RefreshToken findByUserIdAndHashedToken(ObjectId userId, String hashedToken);
    void deleteByUserIdAndHashedToken(ObjectId userId, String hashedToken);
}
