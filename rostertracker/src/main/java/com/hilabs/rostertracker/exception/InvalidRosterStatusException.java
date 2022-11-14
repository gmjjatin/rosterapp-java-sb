package com.hilabs.rostertracker.exception;

public class InvalidRosterStatusException extends RuntimeException {
    public InvalidRosterStatusException(String message){
        super("InvalidRosterStatusException: " + message);
    }
}
