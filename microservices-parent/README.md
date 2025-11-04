Wellness Resource Service Microservice
Project Overview

The Wellness Resource Service is a Spring Boot microservice that manages wellness resources for a 
community platform. It supports storing, retrieving, updating, and deleting resources, and integrates 
with PostgreSQL for persistent storage and Redis for caching to improve performance.

This microservice is containerized with Docker and can run individually or as part of a multi-container setup 
with Docker Compose.

Key Features

CRUD operations on wellness resources (title, description, category, URL)

Redis caching for faster GET requests

PostgreSQL persistence for reliable data storage

Fully containerized for easy deployment

Docker Setup (Recommended)
1. Stop all containers and remove volumes
   docker-compose -p comp3095-wellness -f docker-compose.yml down -v

Stops all containers for the project and removes ephemeral data to ensure a clean start.

2. Rebuild and start containers
   docker-compose -p comp3095-wellness -f docker-compose.yml up -d --build

-d: detached mode

--build: rebuilds images for code or dependency changes

3. Check Spring Boot logs
   docker logs wellness-service

Confirm the service starts successfully

Look for messages indicating DB and cache connections

4. Configure PostgreSQL in pgAdmin

Add a new server:

General Tab: Name: Postgres_Wellness

Connection Tab:

Host: host.docker.internal

Port: 5432

Username: jamesjones

Password: 100898394

Database: wellness-resource-serviceDB

5. Quick verification via Spring Boot

First GET request → queries Postgres

Subsequent GET requests → served from Redis cache

Look for logs like:

Cache 'wellness_resources' miss for key 'all'
Cache 'wellness_resources' hit for key 'all'

6. Verify Redis cache
   docker exec -it wellness-redis redis-cli
   KEYS *

Should return keys like "all", "5", "category:mental health"

Inspect individual keys using GET "<key>"

Running the Service Individually

If you run the app directly from the Dockerfile:

docker build -t wellness-service ./wellness-resource-service
docker run -p 8081:8081 --name wellness-service wellness-service

Runs as a standalone container

Requires an accessible PostgreSQL database and Redis instance

No volumes are mounted; the app is baked into the container

REST API Endpoints
Method	Endpoint	Description
GET	/resources	Get all wellness resources
GET	/resources/{id}	Get a resource by ID
POST	/resources	Create a new resource
PUT	/resources/{id}	Update an existing resource
DELETE	/resources/{id}	Delete a resource by ID

Requests and responses are JSON formatted. Example:

{
"title": "Meditation Guide",
"description": "A beginner-friendly meditation guide",
"category": "Mental Health",
"url": "https://example.com/meditation"
}

Project Notes

Profiles: Use SPRING_PROFILES_ACTIVE=docker in Docker Compose to configure DB and Redis connections automatically.

Postgres Init Script: docker/integrated/postgres/wellness-service/init/init.sql 
initializes the database on container startup.

Testing: Unit and integration tests are included and can be run with:

./gradlew test

Quick Start for Team Members

Stop and clean containers: docker-compose down -v

Build and start: docker-compose up -d --build

Verify Spring Boot logs and Redis cache

Inspect Postgres with pgAdmin

Start interacting with the REST API via Postman or curl

Contact / Support

For issues or questions, contact James Jones, third-year Computer Programming & Analysis student, 
or check the documentation in this repository.