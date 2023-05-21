package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.event.UserActivationDTO;
import com.abranlezama.ecommercestore.service.AccountActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountActivationServiceImp implements AccountActivationService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value("${custom.application.domain}")
    private String domain;
    @Value("${custom.account.activation.endpoint}")
    private String activationEndpoint;

    @Async
    @EventListener
    @Override
    public void sendActivationEmail(UserActivationDTO event) throws IOException {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom("project@abranlezama.com");
        mail.setTo(event.userEmail());
        mail.setSubject("Ecommerce account activation link");

        String emailContent = generateEmailContent(event);
        mail.setText(emailContent);

        javaMailSender.send(mail);
    }

    private String generateEmailContent(UserActivationDTO event) {
       // generate activation link
        String activationLink = domain + activationEndpoint + "?token=" + event.token();
        log.info(activationLink);

        Context context = new Context();
        context.setVariable("userName", event.name());
        context.setVariable("link", activationLink);

        return templateEngine.process("account-activation-template", context);
    }
}
