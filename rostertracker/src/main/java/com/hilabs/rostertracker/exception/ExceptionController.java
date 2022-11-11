package com.hilabs.rostertracker.exception;

import com.hilabs.rostertracker.dto.ErrorResponse;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.NoSuchFileException;
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
    public ResponseEntity<ErrorResponse> exception(Exception exception) {
        log.error("exception {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "stackTrace" + ExceptionUtils.getStackTrace(exception));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NoSuchFileException.class)
    public ResponseEntity<ErrorResponse> exception(NoSuchFileException exception) {
        log.error("exception {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", exception.getMessage() + "stackTrace" + ExceptionUtils.getStackTrace(exception));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> exception(ObjectOptimisticLockingFailureException exception) {
        log.error("ObjectOptimisticLockingFailureException {}", exception.getMessage());
        ErrorResponse errorResponse =  new ErrorResponse("CONCURRENT_USER_UPDATE",
                "Record has already updated by another user");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> exception(OptimisticLockingFailureException exception) {
        log.error("ObjectOptimisticLockingFailureException {}", exception.getMessage());
        ErrorResponse errorResponse =  new ErrorResponse("CONCURRENT_USER_UPDATE",
                "Record has already updated by another user");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}