package com.github.server;

public abstract class BaseDBConnector {
    protected final String uri;

    public BaseDBConnector(String databaseURI) {
        uri = databaseURI;
    }

    public abstract boolean passwordMatches(String username, String password);

    public abstract boolean containsUser(String username);

    public abstract void addUser(String username, String password);
}
