package com.musashi.spring_boot_jwt.service;

import com.musashi.spring_boot_jwt.model.User;
import org.bson.types.ObjectId;

public interface UserService {
    void save(User user);
    User findById(ObjectId userId);
    User findByEmail(String email);
}
