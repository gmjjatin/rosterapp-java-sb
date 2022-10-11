package com.hilabs.rostertracker.service;

import java.util.List;

public interface LdapService {

    void authenticate(final String username, final String password);

    List<String> search(final String username);

    void create(final String username, final String password);

    void modify(final String username, final String password);

    String digestSHA(final String password);
}
