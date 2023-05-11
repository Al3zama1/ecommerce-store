package com.abranlezama.ecommercestore.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

    private int status;
    private String message;
    private String stackTrace;
    private List<UserInputValidationError> errors;

    public ExceptionResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(errors)) this.errors = new ArrayList<>();
        this.errors.add(new UserInputValidationError(field, message));
    }

    private record UserInputValidationError(String field, String message){}
}
