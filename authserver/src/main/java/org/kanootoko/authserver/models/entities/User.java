package org.kanootoko.authserver.models.entities;

import java.io.Serializable;

public interface User extends Serializable {
    public String getLogin();
    public String getRole();

    /**
     * passwordOk is a method which returns true if password is the same as user's and false otherwise.
     * @param password - string to test equality with user's password
     * @return true if password is the same as user's and false otherwise.
     */
    public boolean passwordOk(String password);
}