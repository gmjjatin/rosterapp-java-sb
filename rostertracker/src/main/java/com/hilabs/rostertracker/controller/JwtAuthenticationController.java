package com.hilabs.rostertracker.controller;

import com.hilabs.rostertracker.config.RosterConfig;
import com.hilabs.rostertracker.model.LoginDetails;
import com.hilabs.rostertracker.model.UserDTO;
import com.hilabs.rostertracker.model.jwt.JwtRequest;
import com.hilabs.rostertracker.service.JwtUserDetailsService;
import com.hilabs.roster.entity.RosterUser;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;



@RestController
@Log4j2
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RosterConfig rosterConfig;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<LoginDetails> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest, HttpServletRequest request)
            throws Exception {
        String username=authenticationRequest.getUsername().trim().toLowerCase();
        authenticate(username, authenticationRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(username);
        LoginDetails loginDetails = jwtUserDetailsService.getLoginDetails(userDetails,username,authenticationRequest.getPassword());
        HttpSession session = request.getSession();
        session.setAttribute("userName", username);
        return new ResponseEntity<>(loginDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestBody UserDTO user) throws Exception {
        String userId = user.getUserId().toLowerCase();
        RosterUser isExistingUser = jwtUserDetailsService.findUserbyUserId(userId);
        if(null != isExistingUser  && isExistingUser.getActiveFlag() == 1) {
            log.info("user already exists with the userId");
            return new ResponseEntity<String>("user already exists with the userId", HttpStatus.CONFLICT);
        }
        RosterUser userSaved = jwtUserDetailsService.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public ResponseEntity updateUser(@RequestBody UserDTO user) throws Exception {
        String userId=user.getUserId().toLowerCase();
        RosterUser isExistingUser = jwtUserDetailsService.findUserbyUserId(userId);
        if(null == isExistingUser) {
            log.info("user doesn't exist with the userId");
            return new ResponseEntity<String>("user doesn't exist with the userId", HttpStatus.CONFLICT);
        }
        //update only role
        RosterUser userUpdated = jwtUserDetailsService.updateExisting(isExistingUser,user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public ResponseEntity<RosterUser> updatePassword(@RequestBody UserDTO user) throws Exception {
        String userId=user.getUserId().toLowerCase();
        RosterUser isExistingUser = jwtUserDetailsService.findUserbyUserId(userId);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if(null != isExistingUser && bCryptPasswordEncoder.matches(user.getPassword(), isExistingUser.getPassword())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        jwtUserDetailsService.updatePassword(userId, user.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ResponseEntity<RosterUser> resetPassword(@RequestBody UserDTO user) throws Exception {
        String userId=user.getUserId().toLowerCase();
        jwtUserDetailsService.resetPassword(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public ResponseEntity<RosterUser> deleteUser(@RequestBody UserDTO user) throws Exception {
        String userId=user.getUserId().toLowerCase();
        jwtUserDetailsService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @RequestMapping(value = "/getRoles", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getRoles() throws Exception {
        List<String> roles = jwtUserDetailsService.getRoles();
        return new ResponseEntity<>(roles,HttpStatus.OK);

    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> getUsers() throws Exception {
        List<UserDTO> users = jwtUserDetailsService.getUsers();
        return new ResponseEntity<>(users,HttpStatus.OK);

    }
    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
        // From the HttpRequest get the claims
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");

        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = jwtUserDetailsService.generateRefreshToken(expectedMap, expectedMap.get("sub").toString());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
