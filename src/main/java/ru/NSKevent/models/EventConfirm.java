package ru.NSKevent.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class EventConfirm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer eventId;
    private Date start;
    private Date finish;
    private String email;
    private ModelAction action;

    public EventConfirm() {
    }

    public EventConfirm(Integer eventId, Date start, Date finish, ModelAction action) {
        this.eventId = eventId;
        this.start = start;
        this.finish = finish;
        this.action = action;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ModelAction getAction() {
        return action;
    }

    public void setAction(ModelAction action) {
        this.action = action;
    }
}
