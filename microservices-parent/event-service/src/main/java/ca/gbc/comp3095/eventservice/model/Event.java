package ca.gbc.comp3095.eventservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long eventId;

    // TODO: Check if Getter/Setters are needed
    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private LocalDate date;

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    private int capacity;

    @ManyToMany(cascade = CascadeType.ALL)   // TODO: Determine correct cascade
    @JoinTable(
            name = "student_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Getter
    @Setter
    private List<Student> students = new ArrayList<>();
}
