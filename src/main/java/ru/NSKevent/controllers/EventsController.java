package ru.NSKevent.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import ru.NSKevent.config.MailConfig;
import ru.NSKevent.models.*;
import ru.NSKevent.repositories.EventConfirmRepo;
import ru.NSKevent.repositories.EventRepo;
import ru.NSKevent.repositories.EventVisitorsRepo;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
public class EventsController {

    private final String URL_START = "/api/v001";
    private final long DAY_MILLIS = 86_400_000;

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private EventVisitorsRepo visitorsRepo;
    @Autowired
    private EventConfirmRepo confirmRepo;


    @GetMapping(URL_START + "/events")
    public List<Event> getAllEvents(){
        return eventRepo.findAll();
    }

    @GetMapping(URL_START + "/events/{id}")
    public Event getEventById(@PathVariable(name = "id") Integer id){
        Optional<Event> result = eventRepo.findById(id);
        return result.orElseGet(Event::new);
    }

    //TODO: тест и фикс кидает всё(1 и 10)
    @GetMapping(URL_START + "/eventpage")
    public List<Event> getEventPage(@RequestParam Integer page, @RequestParam Integer limit){
        return eventRepo.getEventsByIdBetween(((page - 1) * limit) + 1, page * limit );
    }

    @PutMapping(URL_START + "/events/{id}")
    public Answer addVisitor(@PathVariable Integer id, @RequestParam(name = "email") String email){
        Optional<EventVisitors> optionalEventVisitors = visitorsRepo.findByEventId(id);
        if (optionalEventVisitors.isPresent()){
            Set<String> visitors = optionalEventVisitors.get().getVisitors();
            if(visitors.contains(email)){
                return new Answer("visitor already added", optionalEventVisitors.get().getEventId());
            }
            else {
                visitors.add(email);
                Event event = eventRepo.findById(optionalEventVisitors.get().getEventId()).get();
                event.setMemberCount(event.getMemberCount() + 1);
                eventRepo.save(event);
                return new Answer("visitor added", id);
            }
}
        else{
            if(eventRepo.findById(id).isPresent()){
                EventVisitors visitors = new EventVisitors();
                visitors.setEventId(id);
                visitors.getVisitors().add(email);
                visitorsRepo.save(visitors);
                return new Answer("visitor added",id);
            }
        }
        return new Answer("event don't found", id);
    }

    @PostMapping(URL_START + "/events")
    public Answer createEvent(@RequestBody Event freshEven){
        Optional<Event> optionalEvent = eventRepo.findByTitle(freshEven.getTitle());
        if (optionalEvent.isPresent()) {
            return new Answer("already exist", optionalEvent.get().getId());
        }
        else{
            eventRepo.save(freshEven);
        }
        return new Answer("Success", eventRepo.findByTitle(freshEven.getTitle()).get().getId());
    }

    @DeleteMapping(URL_START + "/events/{id}")
    public Answer deleteEventById(@PathVariable Integer id, @RequestParam(name = "email") String email){
        Optional<Event> optionalEvent = eventRepo.findById(id);
        if(optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if(event.getAuthor().equals(email)) {
                EventConfirm eventConfirm = new EventConfirm();
                eventConfirm.setAction(ModelAction.DELETE);
                eventConfirm.setEventId(id);
                eventConfirm.setEmail(email);
                Date date = new Date(System.currentTimeMillis());
                eventConfirm.setStart(date);
                eventConfirm.setFinish(new Date(date.getTime() + DAY_MILLIS));
                if (!confirmRepo.existsByEventId(id)) {
                    confirmRepo.save(eventConfirm);
                    new SimpleEmailService().sendSimpleEmailEventConfirm(email, id);
                }
                else {
                    return new Answer("already has action", id);
                }
            }
        }
        return new Answer("success", id);
    }

    @DeleteMapping(URL_START + "/eventvisitor/{id}")
    public Answer deleteVisitorByEventIdAndEmail(@PathVariable Integer id, @RequestParam String email){
        Optional<EventVisitors> optionalEventVisitors = visitorsRepo.getByEventId(id);
        if (optionalEventVisitors.isPresent()) {
            EventVisitors eventVisitors = optionalEventVisitors.get();
            Set<String> visitorsSet = eventVisitors.getVisitors();
            if (visitorsSet.contains(email)) {
                //TODO: VisitorConfirm? Создать его, запомнить и отправить имеил
                visitorsSet.remove(email);
                eventVisitors.setVisitors(visitorsSet);
                visitorsRepo.save(eventVisitors);
                Event event = eventRepo.findById(optionalEventVisitors.get().getEventId()).get();
                event.setMemberCount(event.getMemberCount() - 1);
                eventRepo.save(event);

                return new Answer("success", id);
            } else return new Answer("visitor don't found", id);
        }
        return new Answer("event don't found",id);
    }

    @GetMapping(URL_START + "/confirm")
    public List<EventConfirm> getEventConfirmAll(){
        return confirmRepo.findAll();
    }

    @GetMapping(URL_START + "/visitors")
    public List<EventVisitors> getEventVisitorsAll(){
        return visitorsRepo.findAll();
    }

    public class SimpleEmailService {
        private final String SUBJECT = "Подтверждение действия над "; //+ события/участника
        private final String username = "NSKevent@yandex.ru";
        private final String password = "Tishka1";
        private final String host = "https://" + "localhost:8081";
        private JavaMailSender emailSender;



        public SimpleEmailService(){

        }

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
            //TODO: обработку null
            EventConfirm eventConfirm = confirmRepo.findByEventId(id).get();
            return "Подтвердите действие " + eventConfirm.getAction() + " над событием "
                    + eventRepo.findById(eventConfirm.getEventId()).get().getTitle()
                    + ".\nДля это кого перейдите по ссылке: " + host + "/confirmevent/"
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
}
