package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.dto.StudentResponse;
import ca.gbc.comp3095.eventservice.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    EventResponse createEvent(EventRequest eventRequest);
    List<EventResponse> getAllEvents();
    void updateEvent(Long eventId, EventRequest eventRequest);
    void deleteEvent(Long eventId);

    List<Event> searchEventByLocation(String location);
    List<Event> searchEventByDate(LocalDate date);

    void registerStudent(Long eventId, Long studentId);
    void unregisterStudent(Long eventId, Long studentId);

    List<StudentResponse> findStudentsByEventId(Long eventId);
}
