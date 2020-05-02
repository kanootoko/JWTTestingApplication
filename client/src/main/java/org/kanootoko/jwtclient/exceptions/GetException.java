package org.kanootoko.jwtclient.exceptions;

/**
 * GetException is an exception class which is thrown when get request to the
 * API server fails.
 */
public class GetException extends Exception {

    private static final long serialVersionUID = 28492654061L;

    public GetException() {
        super();
    }

    public GetException(String msg) {
        super(msg);
    }

    public GetException(String msg, Exception ex) {
        super(msg, ex);
    }
}