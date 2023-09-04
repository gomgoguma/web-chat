package com.webchat.config.exception;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseObject<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Bad Request Exception");
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String err = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("\n"));
        return new ResponseObject<>(0, null, err, ResponseConstant.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseObject<?> handleServerError(Exception e) {
        log.warn("Unexpected Exception", e);
        String err = e.getMessage();
        return new ResponseObject<>(0, null, err, ResponseConstant.INTERNAL_SERVER_ERROR);
    }
}