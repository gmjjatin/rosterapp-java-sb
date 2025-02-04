package com.hilabs.rostertracker.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    @Value("${spring.ldap.url}")
    private String ldapUrl;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.username}")
    private String ldapUserName;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUserName);
        contextSource.setPassword(ldapPassword);
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}

