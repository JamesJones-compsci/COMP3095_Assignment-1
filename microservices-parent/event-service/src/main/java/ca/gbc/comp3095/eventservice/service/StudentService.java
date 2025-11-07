package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.dto.StudentRequest;
import ca.gbc.comp3095.eventservice.dto.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse addStudent(StudentRequest studentRequest);
    List<StudentResponse> getAllStudents();
    void updateStudent(Long studentId, StudentRequest studentRequest);
    void removeStudent(Long studentId);

    List<EventResponse> findEventsByStudentId(Long studentId);

}
