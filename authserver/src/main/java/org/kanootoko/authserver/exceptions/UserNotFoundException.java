package org.kanootoko.authserver.exceptions;

public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = 894047281L;

    public UserNotFoundException() {
        super();
    }
    public UserNotFoundException(String msg) {
        super(msg);
    }
    public UserNotFoundException(String msg, Exception ex) {
        super(msg, ex);
    }
}