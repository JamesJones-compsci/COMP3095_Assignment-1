package ca.gbc.comp3095.eventservice.controller;

import ca.gbc.comp3095.eventservice.dto.*;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest eventRequest) {
        EventResponse eventResponse = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getAllEvents() { return eventService.getAllEvents(); }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> searchLocation(@Param("location") String location) {
        List<Event> filteredEvents = eventService.searchEventByLocation(location);

        return filteredEvents.stream()
                .map(this::mapToEventResponse).toList();
    }

    @GetMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> searchDate(@Param("date") String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate convertedDate = LocalDate.parse(date, formatter);

        List<Event> filteredEvents = eventService.searchEventByDate(convertedDate);

        return filteredEvents.stream()
                .map(this::mapToEventResponse).toList();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                         @RequestBody EventRequest eventRequest) {
        eventService.updateEvent(eventId, eventRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/event/" + eventId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventService.deleteEvent(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // TODO: Verity status code
    public void registerEvent(@RequestBody RegisterStudentRequest registerStudentRequest) {
        eventService.registerStudent(registerStudentRequest.eventId(),registerStudentRequest.studentId());
    }

    @DeleteMapping("/unregister")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // TODO: Verity status code
    public void UnregisterEvent(@RequestBody RegisterStudentRequest registerStudentRequest) {
        eventService.unregisterStudent(registerStudentRequest.eventId(), registerStudentRequest.studentId());
    }

    @GetMapping("/listing/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentResponse> getEventListing(@PathVariable("eventId") Long eventId) {
        return eventService.findStudentsByEventId(eventId);
    }

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
}
