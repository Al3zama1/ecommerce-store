package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.event.UserActivationDetails;
import com.abranlezama.ecommercestore.service.AccountActivationService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TwilioAccountActivationService implements AccountActivationService {

    private final TemplateEngine templateEngine;

    @Value("${custom.application.domain}")
    private String domain;
    @Value("${custom.account.activation.endpoint}")
    private String activationEndpoint;

    @Async
    @EventListener
    @Override
    public void sendActivationEmail(UserActivationDetails event) throws IOException {
        // email details
        Email from = new Email("project@abranlezama.com");
        Email to = new Email(event.userEmail());
        String subject = "Account activation token";

        Content content = new Content("text/html", generateEmailContent(event));
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request =  new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        sendGrid.api(request);
    }

    private String generateEmailContent(UserActivationDetails event) {
       // generate activation link
        String activationLink = domain + activationEndpoint + "?token=" + event.token();

        Context context = new Context();
        context.setVariable("userName", event.name());
        context.setVariable("link", activationLink);

        return templateEngine.process("account-activation-template", context);
    }
}
