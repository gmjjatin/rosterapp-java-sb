package com.hilabs.rostertracker.service.impl;

import com.google.gson.Gson;
import com.hilabs.rostertracker.service.LdapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.*;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.DirContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class LdapServiceImpl implements LdapService {


    @Autowired
    private ContextSource contextSource;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Override
    public void authenticate(String username, String password) {
        try {
            DirContext dirContext = contextSource.getContext(username + ldapBase, password);
            try {
                log.info(dirContext.toString());
            } catch (Exception ex) {
                log.error("first" + ex.getMessage());
            }

            try {
                log.info(new Gson().toJson(dirContext));
            } catch (Exception ex) {
                log.error("format" + ex.getMessage());
            }

            try {
                log.info(new Gson().toJson(dirContext.getAttributes("")));
            } catch (Exception ex) {
                log.error("att" + ex.getMessage());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<String> search(String username) {
        return ldapTemplate.search(
                "",
                "cn=" + username,
                (AttributesMapper<String>) attrs -> (String) attrs
                        .get("cn")
                        .get());
    }

    @Override
    public void create(String username, String password) {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "users")
                .add("cn", username)
                .build();
        DirContextAdapter context = new DirContextAdapter(dn);

        context.setAttributeValues("objectclass", new String[]{"top", "person", "organizationalPerson", "inetOrgPerson"});
        context.setAttributeValue("cn", username);
        context.setAttributeValue("sn", username);
        context.setAttributeValue("userPassword", digestSHA(password));
        ldapTemplate.bind(context);
    }

    @Override
    public void modify(String username, String password) {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "users")
                .add("cn", username)
                .build();
        DirContextOperations context = ldapTemplate.lookupContext(dn);

        context.setAttributeValues("objectclass", new String[]{"top", "person", "organizationalPerson", "inetOrgPerson"});
        context.setAttributeValue("cn", username);
        context.setAttributeValue("sn", username);
        context.setAttributeValue("userPassword", digestSHA(password));

        ldapTemplate.modifyAttributes(context);
    }

    @Override
    public String digestSHA(String password) {
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(password.getBytes());
            base64 = Base64
                    .getEncoder()
                    .encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return "{SHA}" + base64;
    }
}
