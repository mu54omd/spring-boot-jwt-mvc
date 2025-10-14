package com.musashi.spring_boot_jwt.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

@Document("refresh_tokens")
public class RefreshToken {
    private ObjectId userId;
    @Indexed(expireAfter = "0s")
    private Instant expiresAt;
    private String hashedToken;
    private Instant createdAt = Instant.now();

    public RefreshToken(ObjectId userId, Instant expiresAt, String hashedToken) {
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.hashedToken = hashedToken;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public void setHashedToken(String hashedToken) {
        this.hashedToken = hashedToken;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(userId, that.userId) && Objects.equals(expiresAt, that.expiresAt) && Objects.equals(hashedToken, that.hashedToken) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, expiresAt, hashedToken, createdAt);
    }
}
