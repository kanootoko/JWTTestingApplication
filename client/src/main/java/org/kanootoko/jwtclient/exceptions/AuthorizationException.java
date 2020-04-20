package org.kanootoko.jwtclient.exceptions;

public class AuthorizationException extends Exception {

    private static final long serialVersionUID = 28492654061L;
    private final Integer status;
    
    public AuthorizationException() {
        super();
        this.status = null;
    }
    public AuthorizationException(Integer status) {
        super();
        this.status = status;
    }
    public AuthorizationException(String msg) {
        super(msg);
        this.status = null;
    }
    public AuthorizationException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
    public AuthorizationException(String msg, Exception ex) {
        super(msg, ex);
        this.status = null;
    }
    public AuthorizationException(String msg, Exception ex, Integer status) {
        super(msg, ex);
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public boolean haveStatus() {
        return status != null;
    }
}