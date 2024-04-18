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
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "mail.infobank.cloud");
        properties.put("mail.smtp.port", "587");
        String emailPassword = System.getenv("SPRING_MAIL_PASSWORD");
        // Authenticate sender's email and password
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("projects@emendatus.lv", emailPassword);
            }
        });

        // Create a new email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("projects@emendatus.lv"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        // Send the email
        Transport.send(message);
    }
}
