package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.model.User;

public class UserMother {

    static User.UserBuilder complete() {
        return User.builder()
                .email("duke.last@gmail.com")
                .password("12345678");
    }
}
