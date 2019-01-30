package ru.NSKevent.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.NSKevent.models.Answer;
import ru.NSKevent.models.Event;
import ru.NSKevent.models.EventConfirm;
import ru.NSKevent.models.ModelAction;
import ru.NSKevent.repositories.EventConfirmRepo;
import ru.NSKevent.repositories.EventRepo;
import ru.NSKevent.repositories.EventVisitorsRepo;

import java.util.Optional;

@CrossOrigin
@RestController
public class ConfirmController {
    private final String URL_START = "/api/v001";

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private EventVisitorsRepo visitorsRepo;
    @Autowired
    private EventConfirmRepo confirmRepo;

    @RequestMapping(URL_START + "/confirmevent/{id}")
    public String confirmEvent(@PathVariable Integer id, @RequestParam String email){
        EventConfirm eventConfirm = confirmRepo.findById(id).get();
        if(eventRepo.findById(eventConfirm.getEventId()).get().getAuthor().equals(email)) {
            if (eventConfirm.getAction() == ModelAction.DELETE) {
                eventRepo.deleteById(id);
                visitorsRepo.deleteByEventId(id);
                confirmRepo.deleteByEventId(id);
                return "Well done!";
            }
        }
        return "Something wrong";
    }

    @RequestMapping(URL_START + "/confirmvisitor/{id}")
    public String confirmVisitor(@PathVariable Integer id, @RequestParam String email){
        EventConfirm eventConfirm = confirmRepo.findById(id).get();
        Optional<Event> event = eventRepo.findById(eventConfirm.getEventId());
        if (visitorsRepo.findByEventId(event.get().getId()).get().getVisitors().contains(email)){
            if (eventConfirm.getAction() == ModelAction.DELETE) {
                //TODO: Другая сущность для списка посетителей?
                visitorsRepo.findByEventId(0);
                confirmRepo.deleteByEventId(id);
                return "Well done!";
            }
        }
        return "Something wrong";
    }

    @DeleteMapping(URL_START + "confirm")
    public Answer deleteAllConfirm(){
        confirmRepo.deleteAll();
        return new Answer("success",0);
    }
}
