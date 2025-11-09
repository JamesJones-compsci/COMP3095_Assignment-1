package ca.gbc.comp3095.wellnessresourceservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WellnessResourceServiceIntegrationTest {

    // Testcontainers PostgreSQL
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void contextLoads() {
        assertThat(restTemplate).isNotNull();
    }
/*
    @Test
    public void testCreateAndGetResource() {
        Map<String, Object> resource = Map.of(
                "title", "Mindfulness Workshop",
                "description", "Learn mindfulness techniques",
                "category", "mindfulness",
                "url", "https://example.com/mindfulness"
        );

        // POST to create resource and get Location header
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/api/resources", resource, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = postResponse.getHeaders().getLocation().toString();
        assertThat(location).isNotNull();

        // Extract ID from Location URL
        String[] parts = location.split("/");
        String resourceId = parts[parts.length - 1];

        // GET the resource by ID
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("/api/resources/" + resourceId, Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().get("title")).isEqualTo("Mindfulness Workshop");
    }

    @Test
    public void testUpdateResource() {
        Map<String, Object> resource = Map.of(
                "title", "Yoga Session",
                "description", "Relax and stretch",
                "category", "fitness",
                "url", "https://example.com/yoga"
        );

        // Create first
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/api/resources", resource, String.class);
        String location = postResponse.getHeaders().getLocation().toString();
        String resourceId = location.split("/")[location.split("/").length - 1];

        // Update resource
        Map<String, Object> updatedResource = Map.of(
                "title", "Yoga Advanced",
                "description", "Advanced techniques",
                "category", "fitness",
                "url", "https://example.com/yoga-advanced"
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updatedResource, headers);
        ResponseEntity<Void> putResponse = restTemplate.exchange("/api/resources/" + resourceId, HttpMethod.PUT, requestEntity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // GET updated resource
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("/api/resources/" + resourceId, Map.class);
        assertThat(getResponse.getBody().get("title")).isEqualTo("Yoga Advanced");
    }

    @Test
    public void testDeleteResource() {
        Map<String, Object> resource = Map.of(
                "title", "Nutrition Talk",
                "description", "Healthy eating",
                "category", "nutrition",
                "url", "https://example.com/nutrition"
        );

        // Create resource
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/api/resources", resource, String.class);
        String location = postResponse.getHeaders().getLocation().toString();
        String resourceId = location.split("/")[location.split("/").length - 1];

        // DELETE resource
        restTemplate.delete("/api/resources/" + resourceId);

        // GET should not contain deleted resource
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/resources/" + resourceId, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
   }
*/
}

