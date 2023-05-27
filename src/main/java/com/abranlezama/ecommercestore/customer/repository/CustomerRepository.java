package com.abranlezama.ecommercestore.customer.repository;

import com.abranlezama.ecommercestore.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String customerEmail);

    boolean existsByEmail(String customerEmail);
}
