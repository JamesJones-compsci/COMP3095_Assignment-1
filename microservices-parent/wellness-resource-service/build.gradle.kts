plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ca.gbc.comp3095"
version = "0.0.1-SNAPSHOT"
description = "wellness-resource-service"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(24))
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot dependencies
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-redis") // Redis support

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")

	// Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")

	// ✅ Testcontainers BOM ensures all modules are aligned
	testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.3"))

	// Testcontainers modules
	testImplementation("org.testcontainers:testcontainers")       // core, includes GenericContainer and Redis support
	testImplementation("org.testcontainers:postgresql")            // PostgreSQL container
	testImplementation("org.testcontainers:junit-jupiter")         // JUnit 5 integration

	testImplementation("io.rest-assured:rest-assured")             // For REST API testing
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Redis client (Jedis) for direct cache validation in integration tests
	testImplementation("redis.clients:jedis:5.1.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
