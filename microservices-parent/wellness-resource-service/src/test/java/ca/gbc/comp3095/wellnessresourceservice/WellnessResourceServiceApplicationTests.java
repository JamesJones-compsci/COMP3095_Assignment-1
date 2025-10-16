package ca.gbc.comp3095.wellnessresourceservice;

import ca.gbc.comp3095.wellnessresourceservice.model.Resource;
import ca.gbc.comp3095.wellnessresourceservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.junit.Assert.assertEquals;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class WellnessResourceServiceApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass");


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

	private Resource createResourceRequest(){
		return new Resource("Mindful Breathing", "Helps calm the mind", "mindfulness", "https://example.com/breathe");
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
				.andExpect(status().isCreated());

		assertEquals(1, resourceRepository.findAll().size());
	}


	@Test
	void ReturnAllResourceTest() throws Exception {

		resourceRepository.save(new Resource("Title1", "Desc1", "mindfulness", "https://url1.com"));
		resourceRepository.save(new Resource("Title2", "Desc2", "counseling", "https://url2.com"));

		mockMvc.perform(get("/api/resources"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}


	@Test
	void updateResourceTest() throws Exception {
		Resource saved = resourceRepository.save(createResourceRequest());
		saved.setTitle("Updated Title");

		mockMvc.perform(put("/api/resources/" + saved.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(saved)))
				.andExpect(status().isNoContent()); // Updated to expect 204

		Resource updated = resourceRepository.findById(saved.getId()).get();
		assertEquals("Updated Title", updated.getTitle());
	}

	@Test
	void deleteResourceTest() throws Exception {
		Resource saved = resourceRepository.save(createResourceRequest());

		mockMvc.perform(delete("/api/resources/" + saved.getId()))
				.andExpect(status().isNoContent()); // Updated to expect 204

		assertEquals(0, resourceRepository.count());
	}

}
