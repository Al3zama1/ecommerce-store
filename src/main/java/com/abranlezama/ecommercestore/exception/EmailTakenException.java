package com.abranlezama.ecommercestore.exception;

public class EmailTakenException extends RuntimeException{

    public EmailTakenException(String message) {
        super(message);
    }
}
