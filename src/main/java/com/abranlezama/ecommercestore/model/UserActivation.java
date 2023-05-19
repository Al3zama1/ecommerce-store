package com.abranlezama.ecommercestore.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class UserActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID token;
    @Column(nullable = false)
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;
}
