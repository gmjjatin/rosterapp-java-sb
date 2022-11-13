package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAAuthPrivilege;
import com.hilabs.roster.repository.RAAuthPrivilegeRepository;
import com.hilabs.rostertracker.config.JwtTokenUtil;
import com.hilabs.rostertracker.dto.RAAuthPrivilegeDTO;
import com.hilabs.rostertracker.model.LoginDetails;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private LdapService ldapService;

    @Value("${isAuthenticationNeeded}")
    private String isAuthenticationNeeded;

    @Autowired
    private RAAuthPrivilegeRepository raAuthPrivilegeRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        //TODO fix it
        return new org.springframework.security.core.userdetails.User(userId, userId, new ArrayList<>());
    }

    public List<RAAuthPrivilege> getUserPrivileges(String username){
        if("false".equals(isAuthenticationNeeded)){
            return raAuthPrivilegeRepository.findAll();
        }
        List<String> userGroupNames = ldapService.getUserGroups(username);

        return raAuthPrivilegeRepository.findGroupPrivileges(userGroupNames);
    }

    public boolean authenticate(String username, String password){
        if("false".equals(isAuthenticationNeeded)){
            return username == null || !username.equals("invalid_user");
        }
        return ldapService.authenticate(username,password);
    }




    public LoginDetails getLoginDetails(UserDetails userDetails, String username) {
        final String token = jwtTokenUtil.generateToken(userDetails);
        List<RAAuthPrivilege> userPrivileges =  getUserPrivileges(username);
        userPrivileges = userPrivileges.stream().filter(privilege -> !"API".equals(privilege.getPrivilegeType())).collect(Collectors.toList());
        List<RAAuthPrivilegeDTO> userPrivDtos = userPrivileges.stream().map(privilege -> RAAuthPrivilegeDTO.getInstance(privilege)).collect(Collectors.toList());


        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setToken(token);
        loginDetails.setUsername(username);
        loginDetails.setPrivileges(userPrivDtos);
        //TODO fix it
        loginDetails.setFirstName(username);
        return loginDetails;
    }


    public String generateRefreshToken(String jwtToken){
        return jwtTokenUtil.doGenerateRefreshToken(jwtToken);
    }

    public static String stringNullCheck(Object result) {
        return result != null ? result.toString() : null;
    }

}