package org.kanootoko.jwtclient.exceptions;

/**
 * PostException is an exception class which is thrown when post request to the
 * API server fails.
 */
public class PostException extends Exception {

    private static final long serialVersionUID = 28492654061L;

    public PostException() {
        super();
    }

    public PostException(String msg) {
        super(msg);
    }

    public PostException(String msg, Exception ex) {
        super(msg, ex);
    }
}