package com.webchat.config.exception;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseObject<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String err = bindingResult.getAllErrors().toString();
        return new ResponseObject<>(0, null, err, ResponseConstant.BAD_REQUEST, null);
    }

    @ExceptionHandler({NullPointerException.class, SQLException.class, RuntimeException.class})
    public ResponseObject<?> handleServerError(Exception e) {
        String err = e.getMessage();
        return new ResponseObject<>(0, null, err, ResponseConstant.INTERNAL_SERVER_ERROR, null);
    }
}