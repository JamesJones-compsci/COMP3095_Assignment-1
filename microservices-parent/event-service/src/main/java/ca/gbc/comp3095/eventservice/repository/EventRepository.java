package ca.gbc.comp3095.eventservice.repository;

import ca.gbc.comp3095.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByLocation(String location);
    List<Event> findByDate(LocalDate date);
}
