package com.abranlezama.ecommercestore.customer.util;

import com.abranlezama.ecommercestore.customer.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomerUserDetails {
    final public static class SecurityCustomer extends Customer implements UserDetails {

        public SecurityCustomer(Customer customer) {
            super(customer);
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Set.of(new SimpleGrantedAuthority("CUSTOMER"));
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
