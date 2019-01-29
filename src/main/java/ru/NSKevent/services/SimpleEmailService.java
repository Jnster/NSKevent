package ru.NSKevent.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.NSKevent.config.MailConfig;

@Service
public class SimpleEmailService {
    private final String SUBJECT = "Подтверждение регистрации"; //+ события/участника

    public JavaMailSender emailSender;

    public String sendSimpleEmail(@RequestParam String username, @RequestParam String password, @RequestParam String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        emailSender = MailConfig.getSslYandexSender(username, password);

        message.setTo(to);
        message.setFrom(username);
        message.setSubject(SUBJECT + "события.");
        message.setText("Здесь нужен текст.");
        //TODO: Код подтверждения через мапинг /ergrffe/{code}

        this.emailSender.send(message);

        return "Email Sent!";
    }
}
