package ru.NSKevent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.NSKevent.models.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event,Integer> {
    List<Event> getEventsByIdBetween(Integer minId, Integer maxId);
    Optional<Event> findByTitle(String title);
}
