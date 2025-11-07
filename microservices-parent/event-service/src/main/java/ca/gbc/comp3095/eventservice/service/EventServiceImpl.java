package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.dto.StudentResponse;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.model.Student;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import ca.gbc.comp3095.eventservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StudentRepository studentRepository;

    @Override
    public EventResponse createEvent(EventRequest eventRequest) {
        log.debug("Creating new event {}", eventRequest);

        Event event = Event.builder()
                .title(eventRequest.title())
                .description(eventRequest.description())
                .date(eventRequest.date())
                .location(eventRequest.location())
                .capacity(eventRequest.capacity())
                .build();

        eventRepository.save(event);

        return new EventResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getCapacity()
        );
    }

    @Override
    public List<EventResponse> getAllEvents() {
        log.debug("Getting all events");

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToEventResponse).toList();
    }

    @Override
    public void updateEvent(Long eventId, EventRequest eventRequest) {
        log.debug("Updating event {}", eventId);

        eventRepository.findById(eventId)
                .map(currentEvent -> {
                    currentEvent.setTitle(eventRequest.title());
                    currentEvent.setDescription(eventRequest.description());
                    currentEvent.setDate(eventRequest.date());
                    currentEvent.setLocation(eventRequest.location());
                    currentEvent.setCapacity(eventRequest.capacity());
                    return eventRepository.save(currentEvent);
                }).orElseThrow(() -> new RuntimeException("There is no event with id " + eventId));
    }

    @Override
    public void deleteEvent(Long eventId) {
        log.debug("Deleting event {}", eventId);

        eventRepository.deleteById(eventId);
    }

    public List<Event> searchEventByLocation(String location) {
        log.debug("Filtering by location {}", location);

        return eventRepository.findByLocation(location);
    }

    public List<Event> searchEventByDate(LocalDate date) {
        log.debug("Filtering by date {}", date);

        return eventRepository.findByDate(date);
    }

    @Override
    public void registerStudent(Long eventId, Long studentId) {
        log.debug("Registering student {} into event {}", studentId, eventId);

        // TODO: Resolve duplication
        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new RuntimeException("There is no student with id " + studentId));   // TODO: Improve this

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("There is no event with id " + eventId));

        event.getStudents().add(student);
        eventRepository.save(event);
    }

    @Override
    public void unregisterStudent(Long eventId, Long studentId) {
        log.debug("Unregistering student {} from event {}", studentId, eventId);

        // TODO: Resolve duplication
        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new RuntimeException("There is no student with id " + studentId));   // TODO: Improve this

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("There is no event with id " + eventId));

        event.getStudents().remove(student);
        eventRepository.save(event);
    }

    @Override
    public List<StudentResponse> findStudentsByEventId(Long eventId) {
        log.debug("Finding students in event {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("There is no event with id " + eventId));

        return event.getStudents().stream()
                .map(this::mapToStudentResponse).toList();
    }

    // TODO: Resolve duplicate method
    private EventResponse mapToEventResponse(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getCapacity()
        );
    }

    // TODO: Resolve duplicate method
    private StudentResponse mapToStudentResponse(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName()
        );
    }
}
