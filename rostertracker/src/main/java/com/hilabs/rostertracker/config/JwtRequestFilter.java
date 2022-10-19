package com.hilabs.rostertracker.config;

import com.hilabs.roster.entity.RAAuthPrivilege;
import com.hilabs.rostertracker.exception.UnAuthorizedException;
import com.hilabs.rostertracker.dto.JWTAuthentication;
import com.hilabs.rostertracker.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                String isRefreshToken = request.getHeader("isRefreshToken");
                String requestURL = request.getRequestURL().toString();
                // allow for Refresh Token creation if following conditions are true.
                if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshtoken")) {
                    allowForRefreshToken(e, request);
                } else {
                    request.setAttribute("exception", e);
                    log.error("JWT Token has expired");
                }

            }
        } else {
            log.warn("JWT Token does not begin with Bearer String");
        }

        //Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            // if token is valid configure Spring Security to manually set authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {


                //get user privileges
                //validate api-url-pattern,method for resource,operation
                //if valid create jwtAuthentication and set securityContext
                // otherwise 403




                List<RAAuthPrivilege> userPrivileges =  jwtUserDetailsService.getUserPrivileges(username);
                System.out.println("UserPrivileges: " + userPrivileges);


                String requestURI = request.getRequestURI();
                String requestMethod = request.getMethod();

                Optional<RAAuthPrivilege> matchingPrivilege = userPrivileges.stream()
                                                    .filter(userPrivilege -> requestMethod.equals(userPrivilege.getOperationType()) && isUrlMatchingWithPattern(userPrivilege.getResourceLocation(),requestURI)).findFirst();

                if(matchingPrivilege.isPresent()){

                    JWTAuthentication jwtAuthentication = new JWTAuthentication(new User(username,"",new ArrayList<>()),
                                                                                jwtTokenUtil.getAllClaimsFromToken(jwtToken),
                                                                                null
                            );
                    jwtAuthentication.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
                    //UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    //        userDetails, null, userDetails.getAuthorities());
                    //usernamePasswordAuthenticationToken
                     //       .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // After setting the Authentication in the context, we specify
                    // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                    //SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
                else{
                    //throw new NullPointerException("Testing");
                    throw new UnAuthorizedException("User does not have access for requested resource.");
                }

            }
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Cache-Control", "no-cache");
        chain.doFilter(request, response);
    }


    private boolean isUrlMatchingWithPattern(String pattern, String url) {
        //Splitting by ? and taking first part so that we ignore query param if any in url and pattern
        String[] patternParts = pattern.split("\\?")[0].split("/");
        String[] urlParts = url.split("\\?")[0].split("/");
        if (patternParts.length != urlParts.length) {
            return false;
        }
        for (int i = 0; i < patternParts.length; i++) {
            String patternPath = patternParts[i];
            String urlPath = urlParts[i];
            if (patternPath.startsWith("{") && patternPath.endsWith("}")) {
                continue;
            }
            if (!patternPath.equals(urlPath)) {
                return false;
            }
        }
        return true;
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        // create a UsernamePasswordAuthenticationToken with null values.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // Set the claims so that in controller we will be using it to create
        // new JWT
        request.setAttribute("claims", ex.getClaims());
    }
}
