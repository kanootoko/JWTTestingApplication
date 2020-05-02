package org.kanootoko.authserver.exceptions;

/**
 * TokenLogicException is an exception class which is thrown when refreshing
 * tokens fails.
 */
public class TokenLogicException extends Exception {

    private static final long serialVersionUID = 2572905029L;

    public TokenLogicException() {
        super();
    }

    public TokenLogicException(String msg) {
        super(msg);
    }

    public TokenLogicException(String msg, Exception ex) {
        super(msg, ex);
    }
}