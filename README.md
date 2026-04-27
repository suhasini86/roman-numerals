# Roman Numerals Converter API

A production-ready Spring Boot REST API that converts integers to Roman
numerals, with full observability, containerization, and Kubernetes
deployment support.

------------------------------------------------------------------------

## 📚 Table of Contents

-   Quick Start
-   API Reference
-   Engineering Methodology
-   Testing Methodology
-   Error Handling
-   Project Layout
-   Observability
-   Deployment
-   Dependency Attribution
-   Future Enhancements

------------------------------------------------------------------------

## 🚀 Quick Start

### Prerequisites

-   Java 17+
-   Maven 3.9+ (or use the included Maven wrapper `./mvnw`)
-   Docker (optional)

### Build

``` bash
./mvnw clean package
```

Build without tests:

``` bash
./mvnw clean package -DskipTests
```

### Run Locally

``` bash
./mvnw spring-boot:run
```

or

``` bash
java -jar target/roman-numerals-0.0.1-SNAPSHOT.jar
```

Application runs at: http://localhost:8080

------------------------------------------------------------------------

## 🐳 Run with Docker

``` bash
docker build -t roman-numerals:latest .
docker run -p 8080:8080 roman-numerals:latest
```

------------------------------------------------------------------------

## 📊 Full Observability Stack

``` bash
cd observability
docker compose up --build
```

  Service                 URL
  ----------------------- -----------------------
  API                     http://localhost:8080

  Prometheus              http://localhost:9090

  Grafana                 http://localhost:3000

  OTel Collector (gRPC)   http://localhost:4317

  OTel Collector (HTTP)   http://localhost:4318

  | Service | URL
|-----------|-----------|
| API |  http://localhost:8080 |
| Prometheus | http://localhost:9090 |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito, REST Assured |
| Observability | Spring Actuator, Prometheus, Micrometer |
| Containerization | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Code Quality | SonarQube, JaCoCo |


------------------------------------------------------------------------

## 🧪 Test the API

``` bash
curl http://localhost:8080/romannumeral?query=42
```

Response:

``` json
{
  "input": "42",
  "output": "XLII"
}
```

------------------------------------------------------------------------

## 📘 API Reference

### GET `/romannumeral?query={integer}`

Converts an integer to Roman numeral.

#### Parameters

  Name    Type     Required   Description
  ------- -------- ---------- ------------------
  query   string   Yes        Integer (1--255)

#### Success Response (200)

``` json
{
  "input": "42",
  "output": "XLII"
}
```

#### Error Response (400)

``` json
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Query range must be between 1 and 255"
}
```

------------------------------------------------------------------------

## ❤️ Health & Metrics

  Endpoint                 Description
  ------------------------ ---------------
  `/actuator/health`       Health status

  `/actuator/info`         App info

  `/actuator/prometheus`   Metrics

------------------------------------------------------------------------

## ⚙️ Engineering Methodology

### Design Principles

  1. Separation of Concerns — Clear layering: Controller → Service → Constants. The
controller handles HTTP concerns (parsing, response building), the service owns
business logic (validation, conversion), and constants are centralized in a single class.

2. Greedy Algorithm — The Roman numeral conversion uses a greedy subtraction
algorithm with parallel value/symbol arrays. This is O(1) — bounded by 13 symbol
iterations regardless of input — making it highly efficient and easy to extend.

3. Defensive Input Handling — Input is accepted as a String, trimmed, and validated at
multiple layers (empty/blank check in controller, range check in service) before
conversion.

4. Centralized Error Handling — A @RestControllerAdvice (GlobalExceptionHandler)
catches all exception types and maps them to structured JSON error responses with
consistent format, timestamps, and HTTP status codes.

5. Observability-First — Integrated from the start with Micrometer, OpenTelemetry tracing,
and Prometheus metrics. Distributed trace IDs (traceId, spanId) are injected into every log
line via the logback pattern.

6. Production-Ready Configuration — Kubernetes manifests include health probes
(liveness, readiness, startup), HPA, PDB, RBAC, network policies, and security contexts.
Inline Documentation

All source classes include Javadoc comments documenting:
● Class-level purpose and design rationale
● Method-level @param, @return, and @throws contracts
● Inline comments for non-obvious logic

------------------------------------------------------------------------

## Testing Methodology

``` bash
./mvnw clean test
./mvnw verify
```

-   JaCoCo 90% coverage enforced
-   Load testing with concurrency

------------------------------------------------------------------------

## Error Handling

Centralized via `@RestControllerAdvice`.

``` json
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Query must be a valid integer"
}
```

------------------------------------------------------------------------

## 📁 Project Layout

    roman-numerals/
    ├── pom.xml
    ├── Dockerfile
    ├── src/
    ├── k8s/
    ├── observability/

------------------------------------------------------------------------

## Future Enhancements

### Extension 1: Range 1--3999

The conversion algorithm already supports the full standard Roman numeral range (1-3999).
The VALUES and SYMBOLS arrays include all mappings up to M (1000). To enable this, only
one constant change is required:
// In RomanNumeralsConstants.java
// Change:
// public static final int MAX_VALUE = 255;
public static final int MAX_VALUE = 3999;
No changes to the conversion logic are needed — only update the constant and corresponding
test assertions.

### Extension 2: Range Query API


Add a new query format for bulk conversion of a range of integers using parallel computation.

Endpoint: GET /romannumeral?min={integer}&max={integer}

### Rules:

● Both min and max are required and must be valid integers=
● min must be less than max
● Both must be within the supported range (1-255, or 1-3999 with Extension 1)
● Conversions are computed in parallel using Java multithreading=
● Results are returned in ascending order

Example Request: GET /romannumeral?min=1&max=3

Example Response:

{

"conversions": [
{ "input": "1", "output": "I" },=
{ "input": "2", "output": "II" },
{ "input": "3", "output": "III" }
]
}

### Implementation Approach:


● Use IntStream.rangeClosed(min, max).parallel() or a ForkJoinPool to compute
conversions concurrently

● Collect results into a sorted list before serializing the response

● Add a RangeConversionResponse DTO with a List<RomanNumeralResponse>

conversions field

● Validate min < max and both within range in the controller/service layer.


------------------------------------------------------------------------

## 🧠 Highlights

-   Clean architecture
-   High test coverage
-   Observability built-in
-   Production-ready
