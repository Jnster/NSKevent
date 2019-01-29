package ru.NSKevent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.NSKevent.models.EventVisitors;

import java.util.Optional;

public interface EventVisitorsRepo extends JpaRepository<EventVisitors, Integer> {
    Optional<EventVisitors> findByEventId(Integer eventId);
    void deleteByEventId(Integer id);
}
