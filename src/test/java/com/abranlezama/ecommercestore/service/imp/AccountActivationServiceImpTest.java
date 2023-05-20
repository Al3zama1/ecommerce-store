package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.event.UserActivationDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AccountActivationServiceImpTest {

    @Mock
    TemplateEngine templateEngine;
    @Mock
    JavaMailSender javaMailSender;
    @Captor
    ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor;
    @InjectMocks
    private AccountActivationServiceImp cut;

    @Test
    void shouldGenerateEmailContent() throws IOException {
        // given
        UserActivationDetails activationDetails = UserActivationDetails.builder()
                .userEmail("duke.last@gmail.com")
                .token(UUID.randomUUID().toString())
                .name("Duke")
                .build();

        // When
        cut.sendActivationEmail(activationDetails);

        // Then
        then(javaMailSender).should().send(messageArgumentCaptor.capture());
        SimpleMailMessage message = messageArgumentCaptor.getValue();

        assertThat(message.getSubject()).isEqualTo("Ecommerce account activation link");
        assertThat(message.getFrom()).isEqualTo("project@abranlezama.com");
        assertThat(Objects.requireNonNull(message.getTo()).length).isEqualTo(1);
        assertThat(Objects.requireNonNull(message.getTo())[0]).isEqualTo(activationDetails.userEmail());
    }

}
