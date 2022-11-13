package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAAuthPrivilege;
import com.hilabs.rostertracker.config.JwtTokenUtil;
import com.hilabs.rostertracker.config.RosterConfig;
import com.hilabs.rostertracker.dto.CollectionResponse;
import com.hilabs.rostertracker.dto.JWTAuthentication;
import com.hilabs.rostertracker.exception.UnAuthorizedException;
import com.hilabs.rostertracker.model.LoginDetails;
import com.hilabs.rostertracker.model.jwt.JwtRequest;
import com.hilabs.rostertracker.service.JwtUserDetailsService;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


@RestController
@Log4j2
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RosterConfig rosterConfig;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;



    @Value("${isAuthenticationNeeded}")
    private String isAuthenticationNeeded;



    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<LoginDetails> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest, HttpServletRequest request) throws Exception {
        boolean isAuthenticated = false;
        String username = authenticationRequest.getUsername() == null ? null : authenticationRequest.getUsername().trim().toLowerCase();
        String password = authenticationRequest.getPassword();
        //if (isAuthenticationNeeded.equalsIgnoreCase("true")) {
            if (username != null) {
                isAuthenticated = jwtUserDetailsService.authenticate(username, password);
                JWTAuthentication jwtAuthentication = new JWTAuthentication(new User(username,"",new ArrayList<>()),
                        null,
                        null
                );
                jwtAuthentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            }
        //} else {
        //    isAuthenticated = username != null && !username.isEmpty() && password != null && !password.isEmpty();
        //}
        if (!isAuthenticated) {
            throw new BadCredentialsException("Unauthorized");
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        LoginDetails loginDetails = jwtUserDetailsService.getLoginDetails(userDetails,username);
        return new ResponseEntity<>(loginDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/user-privileges", method = RequestMethod.GET)
    public ResponseEntity<CollectionResponse<RAAuthPrivilege>> getUserPrivileges(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RAAuthPrivilege> userPrivileges = jwtUserDetailsService.getUserPrivileges(username);
        return new ResponseEntity<>(new CollectionResponse<RAAuthPrivilege>(1, new Integer(userPrivileges.size()),userPrivileges,new Long(userPrivileges.size())), HttpStatus.OK);

    }

//    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
//    public ResponseEntity updateUser(@RequestBody UserDTO user) throws Exception {
//        String userId=user.getUserId().toLowerCase();
//        RosterUser isExistingUser = jwtUserDetailsService.findUserbyUserId(userId);
//        if(null == isExistingUser) {
//            log.info("user doesn't exist with the userId");
//            return new ResponseEntity<String>("user doesn't exist with the userId", HttpStatus.CONFLICT);
//        }
//        //update only role
//        RosterUser userUpdated = jwtUserDetailsService.updateExisting(isExistingUser,user);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
//    public ResponseEntity<RosterUser> updatePassword(@RequestBody UserDTO user) throws Exception {
//        String userId=user.getUserId().toLowerCase();
//        RosterUser isExistingUser = jwtUserDetailsService.findUserbyUserId(userId);
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        if(null != isExistingUser && bCryptPasswordEncoder.matches(user.getPassword(), isExistingUser.getPassword())) {
//            return new ResponseEntity<>(HttpStatus.CONFLICT);
//        }
//        jwtUserDetailsService.updatePassword(userId, user.getPassword());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
//    public ResponseEntity<RosterUser> resetPassword(@RequestBody UserDTO user) throws Exception {
//        String userId=user.getUserId().toLowerCase();
//        jwtUserDetailsService.resetPassword(userId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
//    public ResponseEntity<RosterUser> deleteUser(@RequestBody UserDTO user) throws Exception {
//        String userId=user.getUserId().toLowerCase();
//        jwtUserDetailsService.deleteUser(userId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
        // From the HttpRequest get the claims
        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            String token = jwtUserDetailsService.generateRefreshToken(jwtToken);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        }

        throw new UnAuthorizedException("User does not have access for requested resource..");




//        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");
//
//        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
//        String token = jwtUserDetailsService.generateRefreshToken(expectedMap, expectedMap.get("sub").toString());
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", token);
//        return ResponseEntity.ok(response);
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        if(claims != null){
            for (Entry<String, Object> entry : claims.entrySet()) {
                expectedMap.put(entry.getKey(), entry.getValue());
            }
        }

        return expectedMap;
    }

//    private void authenticate(String username, String password) throws Exception {
//        Objects.requireNonNull(username);
//        Objects.requireNonNull(password);
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
//    }
}
