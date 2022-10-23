package com.hilabs.rostertracker.exception;

import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@ControllerAdvice
@Log4j2
public class ExceptionController {


    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> exception(BadCredentialsException exception) {
        log.error("exception {}", exception.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("UNAUTHENTICATED", "true"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResponseEntity<Map<String, String>> exception(UnAuthorizedException exception) {
        log.error("exception {}", exception.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("UNAUTHORIZED", "true"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exception(Exception exception) {
        log.error("exception {}", exception.getMessage());
        return new ResponseEntity<>("Unexpected error " + exception.getMessage() + " " +
                "stackTrace" + ExceptionUtils.getStackTrace(exception),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}