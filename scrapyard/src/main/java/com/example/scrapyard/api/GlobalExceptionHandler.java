package com.example.scrapyard.api;

import com.example.scrapyard.api.exceptions.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.util.*;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            Exception.class,
            CarNotFoundException.class,
            AuthenticationException.class,
            CustomAuthException.class,
            UsernameExistsException.class,
            DatabaseException.class,
            BrandNotFoundException.class,
            BrandExistsException.class
    })
    @Nullable
    public final ResponseEntity<ApiError> handleCustomException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof CarNotFoundException rnfe) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleRegularException(rnfe, headers, status, request);
        } else if (ex instanceof CustomInternalServerError cise) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleRegularException(cise, headers, status, request);
        } else if (ex instanceof BrandNotFoundException bnfe) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleRegularException(bnfe, headers, status, request);
        } else if (ex instanceof BrandExistsException bee) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleRegularException(bee, headers, status, request);
        } else if (ex instanceof UsernameExistsException usernamee) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleRegularException(usernamee, headers, status, request);
        }else if (ex instanceof AuthenticationException authe){
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleRegularException(authe, headers, status, request);
        }else if (ex instanceof DatabaseException dbe){
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleRegularException(dbe, headers, status, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(e -> (FieldError)e)
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(Collections.singletonList(ex.getMessage())), headers, status, request);
    }

    protected ResponseEntity<ApiError> handleRegularException(Exception ex,
                                                                     HttpHeaders headers, HttpStatus status,
                                                                     WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
