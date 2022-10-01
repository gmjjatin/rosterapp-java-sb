package com.hilabs.rostertracker.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class ExceptionController {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exception(Exception exception) {
        log.error("exception {}", exception.getMessage());
        return new ResponseEntity<>("Unexpected error " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}