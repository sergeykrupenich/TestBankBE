# Banking Microservice API

A production-grade, event-driven banking microservice built with Spring Boot 3, Apache Kafka, PostgreSQL, and Redis. The service provides RESTful endpoints for user authentication, account management, deposits, and money transfers with strict concurrency and idempotency guarantees.

---

## Key Features & Architecture

* **Event-Driven Architecture:** Asynchronous transaction processing via Apache Kafka.
* **Security:** Stateless JWT-based authentication (Spring Security).
* **Concurrency Control:** Pessimistic locking (`PESSIMISTIC_WRITE`) on account balance updates to prevent race conditions during concurrent transfers.
* **Idempotency Support:** Handled via `X-Idempotency-Key` headers on financial operations (`/deposit`, `/transfer`).
* **Caching:** Integrated Redis caching layer for quick data retrieval.
* **API Documentation:** Interactive OpenAPI 3 / Swagger UI interface.

---

## Tech Stack

* **Java:** 17
* **Framework:** Spring Boot 3.2.4
* **Build Tool:** Gradle
* **Database:** PostgreSQL 16
* **Messaging:** Apache Kafka & Zookeeper
* **Cache:** Redis 7
* **Documentation:** SpringDoc OpenAPI / Swagger UI

---

## Key Design Decisions

1. **Transactional Integrity & Pessimistic Locking:**
   Money transfers and deposits utilize database-level pessimistic locking on target account entities to guarantee correctness during simultaneous transaction requests.

2. **Idempotency Mechanism:**
   Financial endpoints accept an optional/required `X-Idempotency-Key` header. Requests are validated to prevent double-spending or duplicate transfers caused by network retries.

3. **Event-Driven Kafka Integration:**
   Transaction events (`TransactionEvent`) are published to Kafka topics, decoupling core balance operations from downstream transaction log/history processors (`transactionservice`).

4. **Modular Package Structure:**
   Clean separation of concerns with domain modules (`accountservice`, `authservice`, `transactionservice`) and shared utilities (`common`).

---

## How to Run the System

### Prerequisites

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
* [JDK 17](https://adoptium.net/) (if running locally without Docker).

### Step 1: Start Infrastructure (PostgreSQL, Kafka, Redis)

Run Docker Compose from the root directory to spin up all required containers:

```bash
docker-compose up -d
```
This starts PostgreSQL on port 5432, Redis on 6379, Zookeeper on 2181, and Kafka on 9092.

### Step 2: Run the Spring Boot Application
Using Gradle Wrapper:
```bash
./gradlew bootRun
```

Or build the JAR and run:

```bash
./gradlew build -x test
java -jar build/libs/*.jar
```

## API Documentation & Testing

### 1. Swagger UI

Once the application is running, open your browser and navigate to:

http://localhost:8080/swagger-ui/index.html

### 2. Postman Collection

A pre-configured Postman collection is included in the project repository:
File: banking-api.postman_collection.json

Quick Testing Flow:

Import banking-api.postman_collection.json into Postman.

Run 1. Authentication -> Register User (automatically extracts and sets the JWT token variable).
Run 2. Happy Path -> Create Source Account and Create Target Account.
