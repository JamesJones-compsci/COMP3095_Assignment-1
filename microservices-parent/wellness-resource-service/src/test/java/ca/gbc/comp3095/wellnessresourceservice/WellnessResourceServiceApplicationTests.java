package ca.gbc.comp3095.wellnessresourceservice;

import ca.gbc.comp3095.wellnessresourceservice.model.Resource;
import ca.gbc.comp3095.wellnessresourceservice.repository.ResourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(properties = "spring.cache.type=none") // Disable caching for tests
@Transactional // Rollback DB after each test
class WellnessResourceServiceApplicationTests {

	// PostgreSQL TestContainer
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass")
			.withReuse(true)
			.waitingFor(Wait.forListeningPort());

	// Configure Spring properties to use TestContainers
	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourceRepository resourceRepository;

	private Resource createResourceRequest() {
		return new Resource(
				"Mindful Breathing",
				"Helps calm the mind",
				"mindfulness",
				"https://example.com/breathe"
		);
	}

	@BeforeEach
	void cleanDatabase() {
		resourceRepository.deleteAll();
	}

	@Test
	void createResourceTest() throws Exception {
		Resource request = createResourceRequest();

		mockMvc.perform(post("/api/resources")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(request.getTitle()))
				.andExpect(jsonPath("$.description").value(request.getDescription()))
				.andExpect(jsonPath("$.category").value(request.getCategory()))
				.andExpect(jsonPath("$.url").value(request.getUrl()));

		// Verify DB state directly
		assertEquals(1, resourceRepository.count());
	}

	@Test
	void returnAllResourceTest() throws Exception {
		resourceRepository.saveAndFlush(new Resource("Title1", "Desc1", "mindfulness", "https://url1.com"));
		resourceRepository.saveAndFlush(new Resource("Title2", "Desc2", "counseling", "https://url2.com"));

		mockMvc.perform(get("/api/resources"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].title").value("Title1"))
				.andExpect(jsonPath("$[1].title").value("Title2"));
	}

	@Test
	void updateResourceTest() throws Exception {
		Resource saved = resourceRepository.saveAndFlush(createResourceRequest());
		saved.setTitle("Updated Title");

		mockMvc.perform(put("/api/resources/" + saved.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(saved)))
				.andExpect(status().isNoContent());

		// Verify DB state directly
		Resource updated = resourceRepository.findById(saved.getId()).orElseThrow();
		assertEquals("Updated Title", updated.getTitle());
	}

	@Test
	void deleteResourceTest() throws Exception {
		Resource saved = resourceRepository.saveAndFlush(createResourceRequest());

		mockMvc.perform(delete("/api/resources/" + saved.getId()))
				.andExpect(status().isNoContent());

		// Verify DB is empty
		assertEquals(0, resourceRepository.count());
	}
}
