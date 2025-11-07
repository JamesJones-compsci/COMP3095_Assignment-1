package ca.gbc.comp3095.eventservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventServiceApplicationTests {

	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		postgres.start();
	}

	@Test
	void createEventTest() {
		String requestBody = """
				{
					"title": "Container Testing",
					"description": "A test performed by a spun up container",
					"date": "2025-11-07",
					"location": "toronto",
					"capacity": 1
				}
				""";

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/events")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.CREATED.value())
				.body("eventId", Matchers.notNullValue())
				.body("title", Matchers.equalTo("Container Testing"))
				.body("description", Matchers.equalTo("A test performed by a spun up container"))
				.body("date", Matchers.equalTo("2025-11-07"))
				.body("location", Matchers.equalTo("toronto"))
				.body("capacity", Matchers.equalTo(1));
	}

	@Test
	void getAllEventsTest() {
		// Populate??
		String requestBody = """
				{
					"title": "Container Testing",
					"description": "A test performed by a spun up container",
					"date": "2025-11-07",
					"location": "toronto",
					"capacity": 1
				}
				""";

		// Create new event
		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/events")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.CREATED.value())
				.body("eventId", Matchers.notNullValue())
				.body("title", Matchers.equalTo("Container Testing"))
				.body("description", Matchers.equalTo("A test performed by a spun up container"))
				.body("date", Matchers.equalTo("2025-11-07"))
				.body("location", Matchers.equalTo("toronto"))
				.body("capacity", Matchers.equalTo(1));

		// Check for at least one account
		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/events")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.OK.value())
				.body("size()", Matchers.greaterThan(0))
				.body("[0].title", Matchers.equalTo("Container Testing"))
				.body("[0].description", Matchers.equalTo("A test performed by a spun up container"))
				.body("[0].date", Matchers.equalTo("2025-11-07"))
				.body("[0].location", Matchers.equalTo("toronto"))
				.body("[0].capacity", Matchers.equalTo(1));
	}

	@Test
	void searchLocationTest() {
		populateEvent();

		RestAssured.given()
				.when()
				.get("/api/events/filter?location=toronto")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.OK.value())
				.body("size()", Matchers.greaterThan(0))
				.body("[0].title", Matchers.equalTo("Container Testing"))
				.body("[0].description", Matchers.equalTo("A test performed by a spun up container"))
				.body("[0].date", Matchers.equalTo("2025-11-07"))
				.body("[0].location", Matchers.equalTo("toronto"))
				.body("[0].capacity", Matchers.equalTo(1));
	}

	@Test
	void searchDateTest() {
		populateEvent();

		RestAssured.given()
				.when()
				.get("/api/events/filters?date=2025-11-07")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.OK.value())
				.body("size()", Matchers.greaterThan(0))
				.body("[0].title", Matchers.equalTo("Container Testing"))
				.body("[0].description", Matchers.equalTo("A test performed by a spun up container"))
				.body("[0].date", Matchers.equalTo("2025-11-07"))
				.body("[0].location", Matchers.equalTo("toronto"))
				.body("[0].capacity", Matchers.equalTo(1));
	}

	@Test
	void updateEventTest() {
		Integer eventId = populateEvent();

		String newEvent = """
				{
					"title": "Container Testing",
					"description": "A test performed by a spun up container",
					"date": "2025-11-07",
					"location": "colombia",
					"capacity": 3
				}
				""";

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(newEvent)
				.when()
				.put("/api/events/{eventId}", eventId)
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value())
				.header("location", "/api/event/" + eventId);

	}

	@Test
	void deleteEventTest() {
		Integer eventId = populateEvent();

		// Delete event
		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/events/{eventId}", eventId)
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value());

		// Test that event was deleted
		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/events")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.OK.value())
				.body("eventId", Matchers.not(Matchers.hasItem(eventId)));
	}

	@Test
	void registerEventTest() {
		Integer eventId = populateEvent();
		Integer studentId = generateStudent();

		String requestBody = """
				{
					"eventId": %d,
					"studentId": %d
				}
				""".formatted(eventId, studentId);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/events/register")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void UnregisterEventTest() {
		Integer eventId = populateEvent();
		Integer studentId = generateStudent();

		String requestBody = """
				{
					"eventId": %d,
					"studentId": %d
				}
				""".formatted(eventId, studentId);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.delete("/api/events/unregister")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void getEventListingTest() {
		// Create an event and 3 students
		Integer eventId = populateEvent();
		Integer studentId1 = generateStudent();
		Integer studentId2 = generateStudent();
		Integer studentId3 = generateStudent();

		// Register 3 students into event
		registerStudentInEvent(eventId, studentId1);
		registerStudentInEvent(eventId, studentId2);
		registerStudentInEvent(eventId, studentId3);

		// Check for 3 registered students
		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/events/listing/{eventId}", eventId)
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.OK.value())
				.body("size()", Matchers.equalTo(3));
	}


	/* HELPER FUNCTIONS */

	private Integer populateEvent() {

		String requestBody = """
				{
					"title": "Container Testing",
					"description": "A test performed by a spun up container",
					"date": "2025-11-07",
					"location": "toronto",
					"capacity": 1
				}
				""";

		return RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/events")
				.then()
				.log().ifValidationFails()
//				.statusCode(HttpStatus.CREATED.value())
				.extract().path("eventId");


//				.body("eventId", Matchers.notNullValue())
//				.body("title", Matchers.equalTo("Container Testing"))
//				.body("description", Matchers.equalTo("A test performed by a spun up container"))
//				.body("date", Matchers.equalTo("2025-11-07"))
//				.body("location", Matchers.equalTo("toronto"))
//				.body("capacity", Matchers.equalTo(1));
	}

	private Integer generateStudent() {

		String requestBody = """
				{
					"firstName": "Iam",
					"lastName": "Student"
				}
				""";

		return RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/students")
				.then()
				.log().ifValidationFails()
				.extract().path("studentId");
	}

	private void registerStudentInEvent(Integer eventId, Integer studentId) {
		String requestBody = """
				{
					"eventId": %d,
					"studentId": %d
				}
				""".formatted(eventId, studentId);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/events/register")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

}
