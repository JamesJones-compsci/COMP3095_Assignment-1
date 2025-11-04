package ca.gbc.comp3095.wellnessresourceservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import redis.clients.jedis.Jedis;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WellnessResourceServiceIntegrationTest {

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);

    static {
        postgresContainer.start();
        redisContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", redisContainer.getFirstMappedPort().toString());
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private Jedis jedis;

    @BeforeEach
    public void setUp() {
        jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
        jedis.flushAll(); // Clear Redis before each test
    }

    @Test
    public void contextLoads() {
        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void testCreateAndGetResource() {
        var resource = Map.of(
                "title", "Mindfulness Workshop",
                "description", "Learn mindfulness techniques",
                "category", "mindfulness",
                "url", "https://example.com/mindfulness"
        );

        // POST to create resource
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/resources", resource, String.class);
        assertThat(postResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // GET resources
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/resources", String.class);
        assertThat(getResponse.getBody()).contains("Mindfulness Workshop");

        // Verify cache
        String cached = jedis.get("wellness_resources::all");
        assertThat(cached).contains("Mindfulness Workshop");
    }

    @Test
    public void testUpdateResource() {
        var resource = Map.of(
                "title", "Yoga Session",
                "description", "Relax and stretch",
                "category", "fitness",
                "url", "https://example.com/yoga"
        );

        // Create first
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/resources", resource, String.class);
        assertThat(postResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // Update resource
        var updatedResource = Map.of(
                "title", "Yoga Advanced",
                "description", "Advanced techniques",
                "category", "fitness",
                "url", "https://example.com/yoga-advanced"
        );
        restTemplate.put("/resources/1", updatedResource);

        // GET updated resource
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/resources/1", String.class);
        assertThat(getResponse.getBody()).contains("Yoga Advanced");

        // Verify cache is updated
        String cached = jedis.get("wellness_resources::1");
        assertThat(cached).contains("Yoga Advanced");
    }

    @Test
    public void testDeleteResource() {
        var resource = Map.of(
                "title", "Nutrition Talk",
                "description", "Healthy eating",
                "category", "nutrition",
                "url", "https://example.com/nutrition"
        );

        // Create resource
        ResponseEntity<String> postResponse = restTemplate.postForEntity("/resources", resource, String.class);
        assertThat(postResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // DELETE resource
        restTemplate.delete("/resources/1");

        // GET should not contain deleted resource
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/resources", String.class);
        assertThat(getResponse.getBody()).doesNotContain("Nutrition Talk");

        // Verify cache eviction
        String cached = jedis.get("wellness_resources::1");
        assertThat(cached).isNull();
    }
}
