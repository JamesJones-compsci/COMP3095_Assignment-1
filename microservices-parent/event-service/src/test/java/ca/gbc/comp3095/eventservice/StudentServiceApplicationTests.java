package ca.gbc.comp3095.eventservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
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
class StudentServiceApplicationTests {

	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

//	@AfterEach
//	void tearDown() {
//		RestAssured.reset();	// TODO: Autocomplete
//		postgres.stop();		// TODO: Got from website
//	}

	static {
		postgres.start();
	}

	@Test
	void addStudentTest() {
		String requestBody = """
				{
					"firstName": "John",
					"lastName": "Doe"
				}
				""";

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/students")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.CREATED.value())
				.body("studentId", Matchers.notNullValue());
	}

	@Test
	void getAllStudentsTest() {
		generateStudent();

		RestAssured.given()
				.when()
				.get("/api/students")
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.OK.value())
				.body("size()", Matchers.greaterThan(0));
	}

	@Test
	void updateStudentTest() {
		Integer studentId = generateStudent();

		String requestBody = """
				{
					"firstName": "Student",
					"lastName": "IsMe"
				}
				""";

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.put("/api/students/{eventID}", studentId)
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void removeStudentTest() {
		Integer studentId = generateStudent();

		RestAssured.given()
				.when()
				.delete("/api/students/{eventID}", studentId)
				.then()
				.log().ifValidationFails()
				.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void getRegisteredEventsTest() {
		// Create student and 3 events
		Integer studentId = generateStudent();
		Integer eventId1 = populateEvent();
		Integer eventId2 = populateEvent();
		Integer eventId3 = populateEvent();

		// Register student for 3 events event
		registerStudentInEvent(eventId1, studentId);
		registerStudentInEvent(eventId2, studentId);
		registerStudentInEvent(eventId3, studentId);

		RestAssured.given()
				.when()
				.get("/api/students/registered/{studentId}", studentId)
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


	/* HELPER FUNCTIONS */

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
				.extract().path("eventId");
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
