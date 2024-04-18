package lv.emendatus.Destiny_PropMan.service.interfaces;

import javax.mail.MessagingException;

public interface EmailService {
    public void sendEmail(String to, String subject, String body) throws MessagingException;
}
