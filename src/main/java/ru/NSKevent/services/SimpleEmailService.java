package ru.NSKevent.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.NSKevent.config.MailConfig;
import ru.NSKevent.models.EventConfirm;
import ru.NSKevent.repositories.EventConfirmRepo;
import ru.NSKevent.repositories.EventRepo;

@Service
public class SimpleEmailService {
    private final String SUBJECT = "Подтверждение действия над "; //+ события/участника
    private final String username = "NSKevent@yandex.ru";
    private final String password = "Tishka1";
    private final String host = "localhost:8081";
    private JavaMailSender emailSender;

    @Autowired
    public EventRepo eventRepo;
    @Autowired
    public EventConfirmRepo confirmRepo;

    /**
     *
     * @param to Адрес отправления
     * @param id Индентификатор подтверждения
     */
    public void sendSimpleEmailEventConfirm(String to, Integer id) {
        SimpleMailMessage message = new SimpleMailMessage();
        emailSender = MailConfig.getSslYandexSender(username, password);

        message.setTo(to);
        message.setFrom(username);
        message.setSubject(SUBJECT + "событием.");
        message.setText(createConfirmEventMessage(to,id));

        this.emailSender.send(message);
    }

    private String createConfirmEventMessage(String to, Integer id){
        EventConfirm eventConfirm = confirmRepo.findByEventId(id).get();
        return "Подтвердите действие " + eventConfirm.getAction() + " над событием "
                + eventRepo.findById(eventConfirm.getEventId()).get().getTitle()
                + ".\nДля этокого перейдите по ссылке: " + host + "/confirmevent/"
                + eventConfirm.getId() + "?email=" + to;
    }

    public void sendSimpleEmailVisitorConfirm(String to, Integer id){
        SimpleMailMessage message = new SimpleMailMessage();
        emailSender = MailConfig.getSslYandexSender(username, password);

        message.setTo(to);
        message.setFrom(username);
        message.setSubject(SUBJECT + "посетителем.");
        message.setText(createConfirmVisitorMessage(to,id));

        this.emailSender.send(message);
    }

    private String createConfirmVisitorMessage(String to, Integer id){
        EventConfirm eventConfirm = confirmRepo.findByEventId(id).get();
        return "Подтвердите действие " + eventConfirm.getAction() + " над посетителем "
                + to + ".\nДля этокого перейдите по ссылке: " + host + "/confirmvisitor/"
                + eventConfirm.getId() + "?email=" + to;
    }
}
