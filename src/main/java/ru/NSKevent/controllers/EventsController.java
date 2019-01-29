package ru.NSKevent.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.NSKevent.models.Answer;
import ru.NSKevent.models.Event;
import ru.NSKevent.models.EventVisitors;
import ru.NSKevent.repositories.EventRepo;
import ru.NSKevent.repositories.EventVisitorsRepo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class EventsController {

    private final String URL_START = "/api/v001";

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private EventVisitorsRepo visitorsRepo;


    @GetMapping(URL_START + "/events")
    public List<Event> getAllEvents(){
        return eventRepo.findAll();
    }

    @GetMapping(URL_START + "/events/{id}")
    public Event getEventById(@PathVariable(name = "id") Integer id){
        Optional<Event> result = eventRepo.findById(id);
        return result.orElseGet(Event::new);
            //TODO: else return JSON {answer:"..."}
    }

    @GetMapping(URL_START + "/eventpage")
    public List<Event> getEventPage(@RequestParam Integer page, @RequestParam Integer limit){
        return eventRepo.getEventsByIdBetween(((page - 1) * limit) + 1, page * limit );
    }

    //TODO: вернуть json
    @PutMapping(URL_START + "/events/{id}")
    public Answer/*json*/ addVisitor(@PathVariable Integer id, @RequestParam String email){
        Optional<EventVisitors> optionalEventVisitors = visitorsRepo.findByEventId(id);
        if (optionalEventVisitors.isPresent()){
            Set<String> visitors =optionalEventVisitors.get().getVisitors();
            if(visitors.contains(email)){
                //TODO: return json answer error
                return new Answer("visitor already added", optionalEventVisitors.get().getEventId());
            }
            else {
                visitors.add(email);
                return new Answer("visitor added", optionalEventVisitors.get().getEventId());
            }
        }
        //TODO:ууужас
        return new Answer("sorry, it is ERROR", optionalEventVisitors.get().getEventId());
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
}
