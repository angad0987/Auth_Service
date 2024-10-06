package com.example.authservice.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.model.UserDto;
import com.example.authservice.response.JwtResponse;
import com.example.authservice.service.CustomUserDetails;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.RefershTokenService;
import com.example.authservice.service.UserDetailsServiceImpl;

@RestController
public class AuthController {

    @Autowired
    public JwtService jwtService;
    @Autowired
    public RefershTokenService refershTokenService;
    @Autowired
    public UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("auth/v1/signup")
    public ResponseEntity signup(@RequestBody UserDto userDto) {
        try {
            String userid = this.userDetailsServiceImpl.signUp(userDto);
            if (Objects.isNull(userid)) {
                return new ResponseEntity<>("User already exist", HttpStatus.BAD_REQUEST);
            }
            String username = userDto.getUserName();
            RefreshToken refreshToken = this.refershTokenService.createRefreshToken(username);
            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(username);
            System.out.println("In signup method username : " + userDetails.getUsername());
            String accessToken = this.jwtService.generateToken(userDetails);
            return new ResponseEntity<>(
                    JwtResponse.builder().userid(userid).accessToken(accessToken).token(refreshToken.getToken())
                            .build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error while signup the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("auth/v1/ping")
    public ResponseEntity<String> ping() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("In ping method");
            UserDetails user = (UserDetails) authentication.getPrincipal();
            String username = user.getUsername();
            Optional<String> useridContainer = this.userDetailsServiceImpl.getUserIdByUsername(username);
            if (useridContainer.isPresent()) {
                return new ResponseEntity<>(useridContainer.get(), HttpStatus.OK);
            } else {
                System.out.println("User unauthorized user id is not present");
                return new ResponseEntity<>("User unauthorized user id is not present", HttpStatus.UNAUTHORIZED);
            }
        } else {
            System.out.println("User unauthorized  authentication is not set");
            return new ResponseEntity<>("User unauthorized authentication is not set", HttpStatus.UNAUTHORIZED);
        }
    }

}
