package ca.gbc.comp3095.goaltrackingservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoalTrackingServiceApplicationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mongoDBContainer.start();
    }

    // Create (put) a Goal
    @Test
    void createGoalTest() {
        String requestBody = """
                {
                    "name": "Morning Jog",
                    "description": "Jog every morning for 30 minutes",
                    "category": "EXERCISE",
                    "frequency": "Daily",
                    "startDate": "2025-11-01",
                    "targetDate": "2025-12-01",
                    "status": "Not Started",
                    "targetValue": 30,
                    "currentValue": 0
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/goals")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("goalId", Matchers.notNullValue())
                .body("name", Matchers.is("Morning Jog"))
                .body("category", Matchers.is("EXERCISE"))
                .body("status", Matchers.is("Not Started"));
    }

    // Get all Goals Test
    @Test
    void getAllGoalsTest() {
        String requestBody = """
                {
                    "name": "Read for 20 minutes",
                    "description": "Daily reading habit",
                    "category": "MENTAL",
                    "frequency": "Daily",
                    "startDate": "2025-11-01",
                    "targetDate": "2025-12-01",
                    "status": "In Progress",
                    "targetValue": 30,
                    "currentValue": 5
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/goals")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("[0].name", Matchers.notNullValue())
                .body("[0].category", Matchers.notNullValue());
    }

    // Helper: Create Goal and Return id
    private String createGoalAndReturnId(String name, String category, double targetValue) {
        String requestBody = """
                {
                    "name": "%s",
                    "description": "Test description",
                    "category": "%s",
                    "frequency": "Weekly",
                    "startDate": "2025-11-01",
                    "targetDate": "2025-12-01",
                    "status": "Not Started",
                    "targetValue": %.2f,
                    "currentValue": 0
                }
                """.formatted(name, category, targetValue);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/goals")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("goalId");
    }

    // Get a Goal by id Test
    @Test
    void getGoalByIdTest() {
        // Create a goal first
        String id = createGoalAndReturnId("Study Java", "MENTAL", 5);

        // Retrieve it by its ID
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("goalId", Matchers.is(id))
                .body("name", Matchers.is("Study Java"))
                .body("category", Matchers.is("MENTAL"))
                .body("targetValue", Matchers.is(5f))
                .body("status", Matchers.notNullValue());
    }

    // Update by id Test
    @Test
    void updateGoalTest() {
        String id = createGoalAndReturnId("Yoga Routine", "EXERCISE", 10);

        String requestBody = """
                {
                    "name": "Yoga Routine",
                    "description": "Evening Yoga",
                    "category": "EXERCISE",
                    "frequency": "Daily",
                    "startDate": "2025-11-05",
                    "targetDate": "2025-12-10",
                    "status": "In Progress",
                    "targetValue": 20,
                    "currentValue": 5
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/goals/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", "/api/goals/" + id);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("find { it.goalId == '%s' }.name".formatted(id), Matchers.is("Yoga Routine"))
                .body("find { it.goalId == '%s' }.status".formatted(id), Matchers.is("In Progress"))
                .body("find { it.goalId == '%s' }.targetValue".formatted(id), Matchers.is(20f));
    }

    // Delete by id Test
    @Test
    void deleteGoalTest() {
        String id = createGoalAndReturnId("Temporary Goal", "MENTAL", 5);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("goalId", Matchers.hasItem(id));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/goals/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("goalId", Matchers.not(Matchers.hasItem(id)));
    }

    // Get by Category Test
    @Test
    void getGoalsByCategoryTest() {
        createGoalAndReturnId("Swim Laps", "EXERCISE", 20);
        createGoalAndReturnId("Meditate", "MENTAL", 10);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals/category/EXERCISE")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("[0].category", Matchers.is("EXERCISE"));
    }

    // Get by Status Test
    @Test
    void getGoalsByStatusTest() {
        String id = createGoalAndReturnId("Learn Piano", "MENTAL", 15);

        // Update goal to mark as completed
        String updateRequest = """
            {
                "name": "Learn Piano",
                "description": "Complete beginner lessons",
                "category": "MENTAL",
                "frequency": "Weekly",
                "startDate": "2025-11-01",
                "targetDate": "2025-12-01",
                "status": "Completed - Excellent Effort",
                "targetValue": 15,
                "currentValue": 15
            }
            """;

        // PUT (update existing goal)
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/goals/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // ✅ FIX: use partial match keyword "completed"
        // this matches regex("completed", "i") in your service
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goals/status/completed")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThan(0))
                .body("[0].status", Matchers.containsStringIgnoringCase("completed"));
    }
}
