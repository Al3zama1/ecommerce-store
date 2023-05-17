package com.abranlezama.ecommercestore;

import com.abranlezama.ecommercestore.model.Role;
import com.abranlezama.ecommercestore.model.RoleType;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.repository.RoleRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        RoleRepository roleRepository) {
        return (args) -> {
            Role employeeRole = roleRepository.findByRole(RoleType.EMPLOYEE).orElseThrow();
            String employeeEmail = "employee@gmail.com";
            if (userRepository.findByEmail(employeeEmail).isEmpty()) {
                User employee = User.builder()
                        .email(employeeEmail)
                        .isEnabled(true)
                        .password(passwordEncoder.encode("12345678"))
                        .roles(Set.of(employeeRole))
                        .build();

                userRepository.save(employee);
            }
        };
    }

}
