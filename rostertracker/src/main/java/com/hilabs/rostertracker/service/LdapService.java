package com.hilabs.rostertracker.service;

import java.util.List;
import javax.naming.directory.*;

public interface LdapService {

    boolean authenticate(final String username, final String password);

    List<Attributes> search(final String username);

    void create(final String username, final String password);

    void modify(final String username, final String password);

    String digestSHA(final String password);
}
