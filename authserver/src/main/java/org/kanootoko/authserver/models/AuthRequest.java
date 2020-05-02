package org.kanootoko.authserver.models;

import java.io.Serializable;

/**
 * AuthRequest is a class that is partly passed on authentication or
 * refresing. When user is authorizing, login and password are set, and when
 * tokens are refreshed, old refresh token is set.
 */
public class AuthRequest implements Serializable {

    private static final long serialVersionUID = 2713840892671892743L;

    private String login;
    private String password;

    private String refreshToken;

    public AuthRequest() {
    }

    public AuthRequest(String login, String password, String refreshToken) {
        this.login = login;
        this.password = password;

        this.refreshToken = refreshToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}