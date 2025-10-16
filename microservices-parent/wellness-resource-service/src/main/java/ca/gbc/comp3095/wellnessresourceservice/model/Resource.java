package ca.gbc.comp3095.wellnessresourceservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;   // e.g. "Mental Health", "Physical Fitness"
    private String url;

    private static final long serialVersionUID = 1L;

    // Convenience constructor for creating new Resources without specifying id
    public Resource(String title, String description, String category, String url) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
    }
}
