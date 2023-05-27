package com.abranlezama.ecommercestore.employee.util;

import com.abranlezama.ecommercestore.employee.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class EmployeeUserDetails {
    final public static class SecurityEmployee extends Employee implements UserDetails {

        public SecurityEmployee(Employee employee) {
            super(employee);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return super.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                    .collect(Collectors.toSet());
        }

        @Override
        public String getUsername() {
            return super.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled();
        }
    }
}
