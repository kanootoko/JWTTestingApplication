package org.kanootoko.jwtclient.exceptions;

public class GetException extends Exception {

    private static final long serialVersionUID = 28492654061L;
    private final Integer status;
    
    public GetException() {
        super();
        this.status = null;
    }
    public GetException(String msg) {
        super(msg);
        this.status = null;
    }
    public GetException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
    public GetException(String msg, Exception ex, Integer status) {
        super(msg, ex);
        this.status = status;
    }
    public GetException(String msg, Exception ex) {
        super(msg, ex);
        this.status = null;
    }
    
    public Integer getStatus() {
        return status;
    }

    public boolean haveStatus() {
        return status != null;
    }
}