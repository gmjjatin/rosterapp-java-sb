package com.hilabs.rostertracker.exception;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message){
        super("UnAuthorized: " + message);
    }
}
