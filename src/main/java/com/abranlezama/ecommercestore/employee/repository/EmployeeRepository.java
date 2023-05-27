package com.abranlezama.ecommercestore.employee.repository;

import com.abranlezama.ecommercestore.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String employeeEmail);
}
