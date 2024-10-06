package com.example.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    public User findByUserName(String username);

}
