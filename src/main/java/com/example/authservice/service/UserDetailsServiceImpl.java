package com.example.authservice.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.authservice.entities.User;
import com.example.authservice.eventProducer.UserInfoEvent;
import com.example.authservice.eventProducer.UserInfoProducer;
import com.example.authservice.model.UserDto;
import com.example.authservice.repositories.UserRepo;
import com.example.authservice.utils.ValidateUtils;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserInfoProducer userInfoProducer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with this username " + username);
        }
        return new CustomUserDetails(user);
    }

    public User checkIfUserAlreadyExist(UserDto userdto) {
        return userRepo.findByUserName(userdto.getUserName());
    }

    public Optional<String> getUserIdByUsername(String username) {
        User user = this.userRepo.findByUserName(username);
        return Optional.of(user.getUserId());
    }

    public String signUp(UserDto userDto) {
        // if (!ValidateUtils.validateUserDetails(userDto)) {
        // return false;
        // }
        if (Objects.nonNull(checkIfUserAlreadyExist(userDto))) {
            return null;
        }
        System.out.println(userDto.getUserName());
        String userid = UUID.randomUUID().toString();
        String password = passwordEncoder.encode(userDto.getPassword());
        String username = userDto.getUserName();
        User newUser = new User(userid, username, password, new HashSet<>());
        System.out.println(newUser.getUserName());
        userRepo.save(newUser);

        // send event to kafka
        UserInfoEvent userInfoEvent = UserInfoEvent.builder().firstName(userDto.getFirstName())
                .lastName(userDto.getLastName()).userId(userid)
                .email(userDto.getEmail()).phoneNumber(Long.valueOf(12345767)).build();

        System.out.println(userInfoEvent.toString());
        userInfoProducer.sendEventToKafka(userInfoEvent);
        return userid;
    }

}
