package org.kanootoko.authserver.models;

import java.io.Serializable;

public class AuthRequest implements Serializable {

    private static final long serialVersionUID = 2713840892671892743L;

    private String login;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
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

    
}