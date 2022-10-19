package com.hilabs.rostertracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.*;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
@Service
@Slf4j
public class LdapService {

    @Autowired
    private ContextSource contextSource;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.url}")
    private String ldapUrl;


    @Value("${spring.ldap.username}")
    private String ldapUsername;

    @Value("${spring.ldap.password}")
    private String ldapPassword;


    private String getDistinguishedName(String username) {
        List<Attributes> attributesList = search("cn=" + username);
        if (attributesList.size() == 0) {
            return null;
        }
        Attributes attributes = attributesList.get(0);
        try {
            return (String) attributes.get("distinguishedName").get();
        } catch (Exception ex) {
            log.error("Error in authenticate {}", ex.getMessage());
            return null;
        }
    }
    public boolean authenticate(String username, String password) {

        if(username == null || "".equals(username) || password == null || "".equals(password)){
            log.error("Empty username or password - {}", username);
            return false;
        }

        String distinguishedName = getDistinguishedName(username);
        if (distinguishedName == null) {
            log.error("User not found - {}", username);
            return false;
        }
        try {
            DirContext dirContext = contextSource.getContext(distinguishedName, password);
            return true;
        } catch (Exception ex) {
            log.error("Error in authenticate {}", ex.getMessage());
            return false;
        }
    }

//    public boolean authenticate(String username, String password) {
//        List<Attributes> attributesList = search("cn=" +username);
//        if (attributesList.size() == 0) {
//            return false;
//        }
//        //TODO Assuming uniqueness for username
//        Attributes attributes = attributesList.get(0);
//        try {
//            DirContext dirContext = contextSource.getContext((String) (attributes.get("distinguishedName").get()), password);
//            return true;
//        } catch (Exception ex) {
//            log.error("Error in authenticate {}", ex.getMessage());
//            return false;
//        }
//    }


    public List<String> getUserGroups(String userName) {
        String distinguishedName = getDistinguishedName(userName);
        List<Attributes> attributesList = search(String.format("(&(objectClass=group)(member=%s))", distinguishedName));
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < attributesList.size(); i++) {
            Attributes attributes = attributesList.get(i);
            if (attributes.get("cn") != null) {
                try {
                    groups.add((String) attributes.get("cn").get());
                } catch (Exception ex) {
                    log.error("CN key not found in - userName {} distinguishedName {}", userName, distinguishedName);
                }
            }
        }
        return groups;
    }
//    public List<String> getUserGroups(final String username){
//        //TODO-SECURITY: Implement below method to return user groups
//        return Arrays.asList("RA_APP_ADMIN_DEV","RA_APP_USER_DEV");
//
//    }


    public List<Attributes> search(String ldapQuery) {
        ldapTemplate.setIgnorePartialResultException(true);
        try {
            return ldapTemplate.search(
                    "" ,
                    ldapQuery,
                    (AttributesMapper<Attributes>) attrs -> attrs
            );
        } catch (Exception ex) {
            log.error("Error in search {}", ex.getMessage());
            return new ArrayList<>();
        }
    }



    private void printAttrs(Attributes attrs) {
        System.out.println(">> printAttrs()");
        if (attrs == null)
        {
            System.out.println("No attributes");
        } else {
            // Print every single attribute
            try
            {
                for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();)
                {
                    Attribute attr = (Attribute) ae.next();
                    System.out.print(attr.getID());

                    // Print values of current attribute
                    NamingEnumeration e = attr.getAll();
                    while(e.hasMore())
                    {
                        String value = e.next().toString();
                        System.out.print("=" + value + ",");
                    }
                }
            } catch (NamingException e) { e.printStackTrace(); }
        }
        System.out.println("<< printAttrs()");
    }

    public void printLdapHierarchy(){

        log.info("Starting printLdapHierarchy");

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl + "/" + ldapBase);

        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);

        DirContext ctx;
        try {
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        //List<String> list = new LinkedList<String>();
        NamingEnumeration results = null;
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search("", "(objectclass=*)", controls);

            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                System.out.println(searchResult.toString());
                Attributes attributes = searchResult.getAttributes();
                printAttrs(attributes);
//                NamingEnumeration ne = attributes.getAll();
//                while(ne.hasMore()){
//                    Attribute attribute = (Attribute)ne.next();
//                    System.out.print
//                attribute.getID()
//                }
//                Attribute attr = attributes.get("cn");
//                String cn = attr.get().toString();
//                list.add(cn);
            }
        } catch (NameNotFoundException e) {
            // The base context was not found.
            // Just clean up and exit.
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                    // Never mind this.
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    // Never mind this.
                }
            }
        }
    }




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
