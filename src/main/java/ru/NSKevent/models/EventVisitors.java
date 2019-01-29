package ru.NSKevent.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "visitor")
public class EventVisitors {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer eventId;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "event_visitors")
    private Set<String> visitors = new HashSet<>();

    public EventVisitors() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Set<String> getVisitors() {
        return visitors;
    }

    public void setVisitors(Set<String> visitors) {
        this.visitors = visitors;
    }
}
