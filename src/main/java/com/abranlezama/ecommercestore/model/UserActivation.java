package com.abranlezama.ecommercestore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class UserActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID token;
    @Column(nullable = false)
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;
    @OneToOne
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActivation that = (UserActivation) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
