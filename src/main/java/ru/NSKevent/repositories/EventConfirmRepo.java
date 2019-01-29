package ru.NSKevent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.NSKevent.models.EventConfirm;

public interface EventConfirmRepo extends JpaRepository<EventConfirm, String> {
}
