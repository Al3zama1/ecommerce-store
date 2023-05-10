package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.model.Role;
import com.abranlezama.ecommercestore.model.RoleType;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.service.imp.TokenServiceImp;
import com.abranlezama.ecommercestore.utils.KeyGeneratorUtility;
import com.abranlezama.ecommercestore.utils.RSAKeyProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith({SpringExtension.class})
@Import(value = {RSAKeyProperties.class, KeyGeneratorUtility.class, TokenServiceImp.class})
class TokenServiceImpTest {

    @MockBean
    private Clock clock;

    @Autowired
    private TokenServiceImp cut;
    @Autowired
    private JwtDecoder jwtDecoder;

    @TestConfiguration
    static class TestConfig {
        @Autowired
        private RSAKeyProperties keys;

        @Bean
        public JwtEncoder jwtEncoder() {
            JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
            JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
            return new NimbusJwtEncoder(jwks);
        }

        @Bean
        public JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
        }
    }


    @Test
    void JwtShouldContainUserAndCorrectRoles() {
        // Given
        Instant instant = Instant.now();
        Clock fixedClock = Clock.fixed(instant, ZoneId.of("UTC"));

        // Generate Authentication token
        Role employee = Role.builder().role(RoleType.EMPLOYEE).build();
        Role admin = Role.builder().role(RoleType.ADMIN).build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                User.builder().email("duke@dev.com").build(), null, Set.of(employee, admin)
        );

        given(clock.instant()).willReturn(fixedClock.instant());

        // When
        String jwt = cut.generateJwt(authentication);

        // Then
        Jwt decodedJwt = jwtDecoder.decode(jwt);
        assertThat(decodedJwt.getClaim("sub").toString()).isEqualTo(authentication.getName());
        assertThat(decodedJwt.getClaim("roles").toString()).contains(" ");
        assertThat(decodedJwt.getClaim("roles").toString()).contains("ADMIN");
        assertThat(decodedJwt.getClaim("roles").toString()).contains("EMPLOYEE");
    }
}
