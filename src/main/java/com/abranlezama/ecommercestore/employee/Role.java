package com.abranlezama.ecommercestore.employee;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Short id;

    @Enumerated(EnumType.STRING)
    EmployeeRoleType role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && this.role == role.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
