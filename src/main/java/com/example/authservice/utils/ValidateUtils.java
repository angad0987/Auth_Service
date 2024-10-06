package com.example.authservice.utils;

import java.util.regex.Pattern;

import com.example.authservice.model.UserDto;

public class ValidateUtils {
    private static final String PASSWORD_PATTTERN = "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static boolean validateUserDetails(UserDto userdto) {
        return validatePassword(userdto.getPassword()) && validateEmail(userdto.getEmail());
    }

    private static boolean validatePassword(String password) {
        return Pattern.matches(PASSWORD_PATTTERN, password);
    }

    private static boolean validateEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }

}
