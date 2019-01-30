package ru.NSKevent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.NSKevent.models.EventConfirm;

import java.util.Optional;

public interface EventConfirmRepo extends JpaRepository<EventConfirm, Integer> {
    void deleteByEventId(Integer id);
    Optional<EventConfirm> findByEventId(Integer id);
}
