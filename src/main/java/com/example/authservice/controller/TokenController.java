package com.example.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.request.AuthRequestDto;
import com.example.authservice.request.RefreshTokenDto;
import com.example.authservice.response.JwtResponse;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.RefershTokenService;
import com.example.authservice.service.UserDetailsServiceImpl;

@RestController
public class TokenController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefershTokenService refershTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("auth/v1/login")
    public ResponseEntity loginUser(@RequestBody AuthRequestDto authRequestDto) {
        Authentication authetication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
        if (authetication.isAuthenticated()) {
            RefreshToken refreshToken = this.refershTokenService.createRefreshToken(authRequestDto.getUsername());
            String accessToken = this.jwtService
                    .generateToken(this.userDetailsServiceImpl.loadUserByUsername(authRequestDto.getUsername()));
            return new ResponseEntity<>(
                    JwtResponse.builder().accessToken(accessToken).token(refreshToken.getToken()).build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public ResponseEntity getAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        try {
            JwtResponse response = refershTokenService.getRefreshToken(refreshTokenDto.getToken())
                    .map(refershTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String accessToken = this.jwtService
                                .generateToken(this.userDetailsServiceImpl.loadUserByUsername(user.getUserName()));
                        return JwtResponse.builder().accessToken(accessToken).token(refreshTokenDto.getToken()).build();
                    }).orElseThrow(() -> new RuntimeException("Refresh token is not IN db"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Refresh Token expired", HttpStatus.UNAUTHORIZED);
        }

    }
}
