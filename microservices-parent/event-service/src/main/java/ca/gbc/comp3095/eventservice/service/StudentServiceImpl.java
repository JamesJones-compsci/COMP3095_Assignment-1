package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.dto.StudentRequest;
import ca.gbc.comp3095.eventservice.dto.StudentResponse;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.model.Student;
import ca.gbc.comp3095.eventservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentResponse addStudent(StudentRequest studentRequest) {
        log.debug("Creating new student {}", studentRequest);

        Student student = Student.builder()
                .firstName(studentRequest.firstName())
                .lastName(studentRequest.lastName())
                .build();

        studentRepository.save(student);

        return new StudentResponse(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName()
        );
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        log.debug("Getting all students");

        List<Student> students = studentRepository.findAll();

        return students.stream()
                .map(this::mapToStudentResponse).toList();
    }

    @Override
    public void updateStudent(Long studentId, StudentRequest studentRequest) {
        log.debug("Updating student {}", studentRequest);

//        studentRepository.findById(studentId).ifPresent(student -> {})

        studentRepository.findById(studentId)
                .map(currentStudent -> {
                    currentStudent.setFirstName(studentRequest.firstName());
                    currentStudent.setLastName(studentRequest.lastName());
                    return studentRepository.save(currentStudent);
                }).orElseThrow(() -> new RuntimeException("There is no student with id " + studentId));
    }

    @Override
    public void removeStudent(Long studentId) {
        log.debug("Deleting student {}", studentId);

        studentRepository.deleteById(studentId);
    }

    @Override
    public List<EventResponse> findEventsByStudentId(Long studentId) {
        log.debug("Finding events student {} is registered in", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("There is no student with id " + studentId));

        return student.getEvents().stream()
                .map(this::mapToEventResponse).toList();
    }

    // TODO: Resolve duplicate method
    private StudentResponse mapToStudentResponse(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName()
        );
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
}
