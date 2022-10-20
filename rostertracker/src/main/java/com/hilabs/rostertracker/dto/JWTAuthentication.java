package com.hilabs.rostertracker.dto;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class JWTAuthentication extends AbstractAuthenticationToken {

    private final Object principal;
    private Map<String, Object> claims;

    public JWTAuthentication(Object principal,Map<String, Object> claims,
                             Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        this.claims = claims;

    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
