package com.example.authservice.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.entities.User;
import com.example.authservice.repositories.RefreshTokenRepo;
import com.example.authservice.repositories.UserRepo;

@Service
public class RefershTokenService {

    @Autowired
    RefreshTokenRepo refreshTokenRepo;
    @Autowired
    UserRepo userRepo;

    public RefreshToken createRefreshToken(String username) {
        User user = this.userRepo.findByUserName(username);
        RefreshToken refreshToken = RefreshToken.builder().user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(360000)).build();
        return refreshTokenRepo.save(refreshToken);

    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(refreshToken);
            throw new RuntimeException(refreshToken.getToken() + "Refresh token is expired");
        }
        return refreshToken;
    }

    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

}
