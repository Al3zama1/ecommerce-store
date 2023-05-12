package com.abranlezama.ecommercestore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
/*
ResponseEntityExceptionHandler is a convenient base class for controller advice classes.
It provides exception handling for internal Spring exceptions. Not extending it means that
all the exceptions will be redirected to DefaultHandlerExceptionResolver and return a
ModelAndView object.
 */
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TRACE = "trace";
    @Value("${custom.stacktrace.trace}")
    private boolean printStackTrace;

    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "User input validation error. Check 'errors' field for details.");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            exceptionResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.unprocessableEntity().body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtExceptions(Exception ex, WebRequest request) {
        log.error("Unknown error occurred.", ex);
        return buildErrorResponse(ex, "Unknown error occurred.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), statusCode, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, String message,
                                                      HttpStatusCode statusCode, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(statusCode.value(), message);

        if (printStackTrace && isTraceOn(request)) {
            exceptionResponse.setStackTrace(Arrays.toString(ex.getStackTrace()));
        }

        return ResponseEntity.status(statusCode).body(exceptionResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value) && value.length > 0 && value[0].contentEquals("true");
    }

}
