package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.UserActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserActivationRepository extends JpaRepository<UserActivation, UUID> {
}
