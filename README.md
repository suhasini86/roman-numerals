# roman-numeral-converter

This project implements a Roman numeral conversion microservice that converts integers to their Roman numeral representation with support for single-value and range-based conversion in a constrained range of 1 to 3999.

The goal of this project is to demonstrate how to design and build a production-ready microservice, including:

● RESTful API design

● Input validation and error handling

● Observability (metrics, health checks)

● CI/CD integration

● Containerized deployment


> Developed by: Suhasini Pamidi

## Table of Contents

- [Problem Statement](#problem-statement)
- [Architecture](#architecture)
- [Frameworks and Technologies Used](#frameworks-and-technologies-used)
- [Packaging Layout](#packaging-layout)
- [How to build and deploy the application](#how-to-build-and-deploy-the-application)
- [Testing](#testing)
- [Rate Limiting](#rate-limiting)
- [Error Handling](#error-handling)
- [CI/CD Pipeline](#cicd-pipeline)
- [Observability](#observability)
- [Sample API Request/Responses](#sample-api-requestresponses)
- [References](#references)

## Problem Statement

Build a REST API service that converts integers to their Roman numeral representation with support for single-value and range-based conversion requests.
### Functional Requirements
#### 1. Single Integer Conversion
Example:
```
/romannumeral?query=10
```
Requirements:

● Only integer-to-Roman numeral conversion is supported.

● Supported input range is 1–3999.

● Inputs outside the supported range are considered invalid.

● Invalid, missing, duplicate, or non-integer query parameters must return appropriate error responses.
#### 2. Range-Based Conversion
Example:
```
/romannumeral?min=1&max=3
```
Requirements:

● Both min and max query parameters are mandatory.

● Supported input range for min and max is 1–3999.

● The max value must be greater than the min value.

● The service must process conversions concurrently using multithreading.

● Results must be returned in ascending order from min to max.

Successful responses must return a JSON payload in the following format:
```
{
  "conversions": [
    { "input": "1", "output": "I" },
    { "input": "2", "output": "II" },
    { "input": "3", "output": "III" }
  ]
}
```

### Non-Functional Requirements
#### 3. Production Readiness / DevOps Capabilities

The application should include production-oriented operational capabilities such as:

● Structured logging

● Metrics and monitoring support

● Centralized exception handling

● Health/readiness endpoints

● Docker containerization for consistent deployment and execution

The service should be designed with maintainability, scalability, and operational observability in mind.

## Architecture

![img_1.png](images/architecture.png)

## Frameworks and Technologies Used


| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21      |
| Framework | Spring Boot | 3.5.14  | 
| Build Tool | Maven | 3.9.2   |
| Testing | JUnit 5, Mockito, REST Assured | Latest  |
| Observability | Spring Actuator, Prometheus, Micrometer | Latest  |
| Containerization | Docker | Latest  |
| CI/CD | GitHub Actions | -       |
| Code Quality | SonarQube, JaCoCo | Latest  |

## Packaging Layout

![img_2.png](images/packaging.png)

## How to build and deploy the application

1. Clone the git repo

```
git clone https://github.com/suhasini86/roman-numerals.git
```

2. Prerequisites for required frameworks

```
java -version
docker -v
docker-compose -v
mvn -v
```

3.  Ensure that Docker is running on your local machine. From the root directory of the application’s Git repository, execute the below commands to start the whole application stack along with the observability capabilities as shown in the architecture diagram.
```
cd observability
```
```
docker-compose pull
```
```
docker-compose up -d
```

The docker compose will spin the whole docker infra. The
whole process should take around 2 to 3 minutes depending on the underlying machine. Output will be as shown below, with status of various services:

![img_3.png](images/grafana/docker-compose-status.png)

Use the below command to refresh the status of the services until all the services are up.

```
docker-compose -f docker-compose.yml ps
```

4. Verify if the deployed services are up and running,

> roman-numeral-converter App - http://localhost:8080/actuator/health
> 
> Swagger UI - http://localhost:8080/swagger-ui.html
> 
> OpenAPI Specification - http://localhost:8080/v3/api-docs
> 
> Prometheus - http://localhost:9090/
>
> Grafana - http://localhost:3000/
> 
> Jaeger - http://localhost:16686/
> 

5. To run the application in standalone mode without DevOps or observability capabilities, execute the following commands in the terminal. This will start the application using the standalone profile, which is configured to operate without observability features.

PowerShell
``` bash
> $env:SPRING_PROFILES_ACTIVE = "standalone"
> mvn spring-boot:run
````

Mac/Linux

``` bash
> export SPRING_PROFILES_ACTIVE=standalone
> mvn spring-boot:run

```

## Testing
The application includes comprehensive testing coverage across functional, integration, and performance dimensions to ensure reliability, correctness, and scalability.

### Health check

> http://localhost:8080/actuator/health

### Sample test

#### Single Integer Conversion Test

> http://localhost:8080/romannumeral?query=255

Output:

```
{
"input": "255",
"output": "CCLV"
}
```
#### Range-Based Conversion Test
> http://localhost:8080/romannumeral?min=1&max=5

Output:

```
{
"conversions": [
    { "input": "1", "output": "I" },
    { "input": "2", "output": "II" },
    { "input": "3", "output": "III" },
    { "input": "4", "output": "IV" },
    { "input": "5", "output": "V" }
  ]
}
```


### Running Unit/Integration Tests
Execute the following commands from the root directory of the application:
``` bash
From application root directory: 

# Run all tests (unit + integration)
    mvn clean test

# Run full verification (includes integration tests)
    mvn verify
    
# Run a specific test class
    mvn test -Dtest=RomanNumeralConverterServiceTest
``` 

### Load Testing
Performance testing is implemented to validate system behavior under concurrent load conditions.

#### Run load tests
Use the below command to run a load test on this API
``` bash
    mvn test -DexcludedGroups="" -DincludedGroups=loadtest
```

The `RomanNumeralConverterLoadTest` simulates 400 requests with 20 concurrent threads, including an initial warm-up phase. The test enforces the following performance criteria:

● Zero request failures

● Average response time < 250 ms

These thresholds ensure the service maintains both stability and responsiveness under load.
![img_4.png](images/load-test.png)

### Code Coverage
Code coverage is enforced using JaCoCo to maintain high test quality and prevent regression risks:

● Minimum 90% line coverage enforced during the build lifecycle

● Coverage reports generated post-test execution

#### Generate and view coverage report
```bash
    mvn clean test
 ```
Open the report:
``` bash
    open target/site/jacoco/index.html
 ```

| Category    | Class                                 | Description                          |
|-------------|---------------------------------------|--------------------------------------|
| Unit Tests  | RomanNumeralConverterServiceTest      | Tests conversion logic               |
| Unit Tests  | RomanNumeralsConstantsTest            | Verifies constant values             |
| Unit Tests  | RomanNumeralControllerUnitTest        | Controller logic with mocked service |
| WebMvc Tests| RomanNumeralControllerWebMvcTest      | HTTP layer testing                   |
| Integration | RomanNumeralControllerIntegrationTest | Full Spring Boot context             |
| Integration | RomanNumeralsApplicationTests         | Application context loads            |
| Exception   | GlobalExceptionHandlerTest            | Exception handling coverage          |
| Exception   | InvalidRequestExceptionTest           | Custom validation exception behavior |
| Load Tests  | RateLimitingFilterTest                | Rate limiting filter logic           |
| Load Tests  | RateLimitPropertiesTest               | Rate limit configuration propereties |
| Load Tests  | RomanNumeralConverterLoadTest         | Concurrent load testing              |

------------------------------------------------------------------------

## Sample API Request/Responses

### Successful Request
#### Single Integer Conversion
```
curl -X GET "http://localhost:8080/romannumeral?query=200"
```

### Successful Response

```
Http Status Code - 200
{
  "input": "200",
  "output": "CC"
}
```

#### Range-Based Conversion Test
> http://localhost:8080/romannumeral?min=1&max=5

Successful Response:

```
{
"conversions": [
{ "input": "1", "output": "I" },
{ "input": "2", "output": "II" },
{ "input": "3", "output": "III" },
{ "input": "4", "output": "IV" },
{ "input": "5", "output": "V" }
]
}
```

### Error Request
Single integer conversion with invalid input
```
curl -X GET "http://localhost:8080/romannumeral?query=abc"
```
### Error Response

```
Http Status Code - 400
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "query value must be a valid integer between 1 and 3999."
}
```

Ranger Based Request with invalid input
```
curl -X GET "http://localhost:8080/romannumeral?min=10&max=abc"
```
### Error Response

```
Http Status Code - 400
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": ""max value must be a valid integer between 1 and 3999."
}
```
### Not Found Request

```bash
curl -X GET "http://localhost:8080/dfgdfgdf"
```

### Not Found Response
```
Http Status Code - 404
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Requested resource not found"
}
```
### Method Not Allowed Request

```
curl -X POST "http://localhost:8080/romannumeral?query=42"
```

### Method Not Allowed Response
```
Http Status Code - 405
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 405,
  "error": "Method Not Allowed",
  "message": "HTTP method not supported"
}
```
### Rate Limit Exceeded Request
When more than 50 requests are sent from the same IP within 60 seconds:

```
curl -X GET "http://localhost:8080/romannumeral?query=42"
```

Rate Limit Exceeded Response

```
Http Status Code - 429
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again after 45 seconds."
}
```
## Rate Limiting

The application includes built-in per-IP rate limiting to protect the service from excessive requests and ensure fair usage across clients.

### How it works:
Rate limiting is implemented as a servlet filter (`RateLimitingFilter`) using a fixed-window counter algorithm. Each unique client IP address is tracked independently - when a client exceeds the configured request limit within the time window, subsequent requests receive a `429 Too Many Requests` response until the window resets.

### Configuration:
Rate limiting is configured in `application.yaml` and can be adjusted without code changes:

```
rate-limit:
enabled: true        # Set to false to disable rate limiting
max-requests: 50     # Maximum requests allowed per time window per IP
time-window-seconds: 60  # Time window duration in seconds
```

 Property                        | Default | Description                                    |
|---------------------------------------|---------|------------------------------------------------|
| rate-limit.enabled | true    | Enable or disable rate limiting globally           |
| rate-limit.max-requests   | 50      | Maximum number of requests per IP per time window |
| rate-limit.time-window-seconds        | 60      | Duration of the rate limit window in seconds   |

Response Headers:

Every API response includes rate limit headers for client visibility:

 Header                        | Default                                 |
|---------------------------------------|--------------------------------|
| X-Rate-Limit-Limit | Maximum requests allowed in the current window  |
| X-Rate-Limit-Remaining   | Number of requests remaining in the current window |
| Retry-After        | Seconds to wait before retrying (only on 429 responses) |


Rate Limit Exceeded Response

```
Http Status Code - 429
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again after 45 seconds."
}
```
#### Excluded Endpoints
The following endpoints are not rate limited:

* /actuator/* - Health checks and monitoring
* /swagger-ui/* - API documentation UI
* /v3/api-docs - OpenAPI specification

#### Client IP Detection
The filter supports proxy-aware IP detection. When running behind a load balancer or reverse proxy, the client IP is extracted from the 'X-Forwarded-For' header. If not present, the direct remote address is used.


## Error Handling

The application leverages a centralized exception handling mechanism implemented via @RestControllerAdvice (GlobalExceptionHandler) to ensure consistent and structured error responses across all endpoints. This approach standardizes API behavior by mapping common validation and request-processing exceptions to appropriate HTTP status codes, while returning a uniform JSON error payload for improved client-side handling and debuggability.

The following table outlines the supported exception mappings, corresponding HTTP status codes, and typical scenarios:

| Exception Type                      | HTTP Status | Scenario                                                   |
|-------------------------------------|------------|------------------------------------------------------------|
| UnsatisfiedServletRequestParameterException | 400        | Invalid parameter combination (query with min/max)         |
| MethodArgumentTypeMismatchException | 400        | Empty/blank query, or value outside range                  |
| ConstraintViolationException        | 400        | Value outside supported range (1-3999)                     |
| InvalidRequestException             | 400        | Custom validation failures (empty params, invalid min/max) |
| NoHandlerFoundException             | 404        | Malformed request body                                     |
| HttpRequestMethodNotSupportedException | 405     |  Unsupported HTTP method (e.g., POST /romannumeral)        |
| Rate limit exceeded                 | 429        | Too many requests from a single IP within the time window  |
| Exception                           | 500        | Any unhandled exception                                    |

``` json
{
  "timestamp": "2026-04-27T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Query must be a valid integer between 1 and 3999."
}
```

## Observability

This project includes simple observability to monitor application behavior using metrics, traces, and logs.
> **Note**: In a production environment, actuator endpoints would be secured via network isolation (separate management port) or Spring Security to restrict access to authorized monitoring systems only. For demonstration purposes, actuator endpoints are exposed without authentication in this project.
### Overview

### Metrics
- Metrics are exposed using Spring Boot Actuator and Micrometer
- Prometheus collects:
    - Request count
    - Response time
    - Error rates
### Traces
- Distributed tracing is enabled using OpenTelemetry
- Helps track request flow
### Logs
- Logs are generated using Logback
- Includes `traceId` and `spanId`
- Logs are sent using Promtail → Loki → Grafana
---
### Data Flow
### Metrics
Application → Actuator → Prometheus → Grafana
### Traces
Application → OpenTelemetry → Collector → Jaeger
### Logs
Application → Logback → Promtail → Loki → Grafana

---

### Grafana setup

> http://localhost:3000/

1. The default Grafana login credentials are admin/admin. For security purposes, you will be prompted to set a new password upon your first login. After successfully signing in, you should see a home screen similar to the one shown below.
   ![img_8.png](images/grafana/grafana-main-screen.png)

2. Preconfigured data sources for Prometheus, Loki, and Jaeger are already available. To review them, navigate to Configuration → Data Sources in the left-hand panel, where you can view and manage the existing configurations.
![img_10.png](images/grafana/grafana-datasource.png)

3. `Metrics:` To explore application metrics, navigate to Drilldown → Metrics in the left-hand panel. This view displays all available metrics. You can refine the results using the search bar—for example, enter http.server.requests to view HTTP server request metrics, as illustrated below.
![img_9.png](images/grafana/grafana-metrics.png)
   Preconfigured metrics dashboard is also available under Dashboards. To access follow the steps as shown in below screenshots.
   ![img_10.png](images/grafana/custom-dashboard.png)
   ![img_10.png](images/grafana/custom-dashboard-2.png)

4. `Logs:` To access application logs, navigate to Drilldown → Logs in the left-hand panel. This view displays all available logs.

    ![img_10.png](images/grafana/grafana-logs-1.png)

    You can filter logs using labels—for example, to view logs for the roman-numeral-converter service, apply the filter service="roman-numeral-converter", as shown below.
    ![img_10.png](images/grafana/grafana-logs-2.png)

5. `Traces:` To explore distributed traces, navigate to Explore from the left-hand panel and select the Jaeger data source. You can retrieve the relevant `TraceId` from logs or metrics. Enter the traceId in the search field and execute the query to visualize the trace details for a specific request. For example, to view traces for the request with traceId = e370fa9a82fb65f4f554bdba20fa9772, enter the value in the search box and click Search, as illustrated below.
   ![img_11.png](images/grafana/grafana-traces.png)


## CI/CD Pipeline

### GitHub Actions Workflow

The CI/CD pipeline includes:

1. **Build & Test**
    - Compile code
    - Run unit tests
    - Run integration tests
    - Code coverage

2. **Docker Build & Push**
    - Build multi-stage Docker image
    - Push to container registry
    - which can be used to deploy the application in any environment (dev, staging, prod)
    - Sample Docker hub registry screenshot:
   - ![img_12.png](images/docker-hub-registry.png)

3. **Security Scanning**
    - Trivy vulnerability scan
    - SARIF report upload

4. **Code Quality**
    - SonarQube analysis
    - Code coverage metrics

### Pipeline Status
Sample workflow run from github actions,
![img_12.png](images/ci-cd.png)

## References

1. [Roman Numeral Wikipedia Reference](https://simple.wikipedia.org/wiki/Roman_numerals)
2. [spring-boot](https://spring.io/projects/spring-boot)
3. [docker-compose](https://docs.docker.com/compose/)
4. [Grafana-Prometheus-Loki-Data Sources](https://grafana.com/docs/grafana/latest/datasources/)
