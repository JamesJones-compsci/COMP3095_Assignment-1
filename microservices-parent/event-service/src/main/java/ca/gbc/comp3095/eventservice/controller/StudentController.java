package ca.gbc.comp3095.eventservice.controller;

import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.dto.StudentRequest;
import ca.gbc.comp3095.eventservice.dto.StudentResponse;
import ca.gbc.comp3095.eventservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StudentResponse> addStudent(@RequestBody StudentRequest studentRequest) {
        StudentResponse studentResponse = studentService.addStudent(studentRequest);
        return new ResponseEntity<>(studentResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StudentResponse> getAllStudents() { return studentService.getAllStudents(); }

    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable("studentId") Long studentId,
                                           @RequestBody StudentRequest studentRequest) {
        studentService.updateStudent(studentId, studentRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/students/" + studentId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> removeStudent(@PathVariable("studentId") Long studentId) {
        studentService.removeStudent(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/registered/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getRegisteredEvents(@PathVariable("studentId") Long studentId) {
        return studentService.findEventsByStudentId(studentId);
    }
}
