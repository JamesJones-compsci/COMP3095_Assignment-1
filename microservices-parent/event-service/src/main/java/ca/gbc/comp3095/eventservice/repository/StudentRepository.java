package ca.gbc.comp3095.eventservice.repository;

import ca.gbc.comp3095.eventservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> { }
