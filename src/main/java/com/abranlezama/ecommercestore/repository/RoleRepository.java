package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.Role;
import com.abranlezama.ecommercestore.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(RoleType role);
}
