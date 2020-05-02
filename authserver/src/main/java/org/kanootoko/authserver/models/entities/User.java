package org.kanootoko.authserver.models.entities;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * User is a sample user class which stores user's login, password and role
 */
public class User implements Serializable {

    private static final long serialVersionUID = 290572655104L;

    private String login;
    private String password;
    private String role;

    public User(@NotNull @NotEmpty String login, @NotNull @NotEmpty String password, @NotNull @NotEmpty String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    /**
     * passwordOk is a method which returns true if password is the same as user's and false otherwise.
     * @param password - string to test equality with user's password
     * @return true if password is the same as user's and false otherwise.
     */
    public boolean passwordOk(String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        return String.format("(User): %s, %s, %s", login, password, role);
    }
}