package com.hilabs.rostertracker.service;

import com.hilabs.rostertracker.config.JwtTokenUtil;
import com.hilabs.rostertracker.model.LoginDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        //TODO fix it
        return new org.springframework.security.core.userdetails.User(userId, userId, new ArrayList<>());
    }


    public LoginDetails getLoginDetails(UserDetails userDetails, String username, String pwd) {
        final String token = jwtTokenUtil.generateToken(userDetails);
        //TODO fix it
        String role = "USER";
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setToken(token);
        loginDetails.setUsername(username);
        loginDetails.setRoleCD(role);
        //TODO fix it
        loginDetails.setFirstName(username);
        loginDetails.setIsDefaultPassword(false);
        return loginDetails;
    }


    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        return jwtTokenUtil.doGenerateRefreshToken(claims, subject);
    }

    public static String stringNullCheck(Object result) {
        return result != null ? result.toString() : null;
    }

}