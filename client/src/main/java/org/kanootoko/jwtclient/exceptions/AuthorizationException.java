package org.kanootoko.jwtclient.exceptions;

/**
 * AuthorizationException is an exception class which is thrown when
 * authentication/authorization or refresh token process fails.
 */
public class AuthorizationException extends Exception {

    private static final long serialVersionUID = 28492654061L;

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String msg) {
        super(msg);
    }

    public AuthorizationException(String msg, Exception ex) {
        super(msg, ex);
    }
}