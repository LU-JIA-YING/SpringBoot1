package com.example.Spring.controller.dto.exception;

import com.example.Spring.controller.dto.response.ErrorResponse;
import org.hibernate.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;

//  https://hackmd.io/@IDdlPCCwQoeX-9DvmEbLyw/HJIc1cRWo
@RestControllerAdvice
public class ApplicationExceptionHandler {


    // 捕捉 Exception
    // 因為是所有例外的父類，可以作為例外處理的最後一道防線
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handler(Exception e) {

        ErrorResponse error = new ErrorResponse(e);

        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }

    // 捕捉 MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handler(MethodArgumentNotValidException e) {

        ErrorResponse error = new ErrorResponse(e);

        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }

    // 捕捉 ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handler(ConstraintViolationException e) {

        ErrorResponse error = new ErrorResponse(e);

        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(DateTimeException.class)
//    public ResponseEntity<ErrorResponse> dateTimeExceptionHandler(DateTimeException e){
//
//        ErrorResponse error = new ErrorResponse(e);
//
//        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
//    }
}
