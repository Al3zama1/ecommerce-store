package com.abranlezama.ecommercestore.exception;

public class EmptyOrderException extends RuntimeException{
    public EmptyOrderException(String message) {
        super(message);
    }
}
