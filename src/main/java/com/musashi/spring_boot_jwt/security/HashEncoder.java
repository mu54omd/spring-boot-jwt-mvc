package com.musashi.spring_boot_jwt.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HashEncoder {
    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public String encode(String raw){
        return bcrypt.encode(raw);
    }
    public Boolean matches(String raw, String hashed){
        return bcrypt.matches(raw, hashed);
    }
}
