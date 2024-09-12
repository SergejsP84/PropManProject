package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.service.interfaces.EmailService;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class JpaEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) throws MessagingException {
// Set mail server properties
        String emailHost = System.getenv("PROPMAN_MAIL_HOST");
        String emailPort = System.getenv("PROPMAN_MAIL_PORT");
        String emailUsername = System.getenv("PROPMAN_MAIL_USERNAME");
        String emailPassword = System.getenv("SPRING_MAIL_PASSWORD");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", emailHost);
        properties.put("mail.smtp.port", emailPort);
        // Authenticate sender's email and password
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        // Create a new email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailUsername));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        // Send the email
        Transport.send(message);
    }
}
