package com.loyai.loyaiproject.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errorHandler = new HashMap<>();
        e.getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errorHandler.put(fieldName,message);
        });

        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception e, HttpServletRequest request){
        return getErrorResponseResponseEntity(e, request,HttpStatus.NOT_FOUND.value());
    }
    @ExceptionHandler(ServiceUnAvailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableExceptions(Exception e, HttpServletRequest request){

        return getErrorResponseResponseEntity(e, request,HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> genericExceptions(Exception e, HttpServletRequest request){
        return getErrorResponseResponseEntity(e, request,HttpStatus.NOT_FOUND.value());
    }

    private ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(Exception e, HttpServletRequest request,int statusCode) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setUrl(request.getRequestURI());
        errorResponse.setStatusCode(statusCode);

        return new ResponseEntity<>(errorResponse,HttpStatus.valueOf(statusCode));
    }
}
