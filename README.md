# Roman Numerals Converter Microservice
Description:
A Spring Boot microservice that converts integer to Roman numeral, built with full observabilty and extensive test coverage.
This is a Spring Boot microservice that converts integers to Roman numerals. It is built with full observability and extensive test coverage.

# Features:
- Converts integers to Roman numerals.
- Full observability with logging, metrics, and tracing.
- Extensive test coverage with unit and integration tests.
- Dockerized application.
- Kubernetes deployment configuration included.

# Technologies Used:
- Java 17
- Spring Boot
- Spring Web for RESTful API
- Maven for build management
- Logback for logging
- Docker for containerization
- Prometheus and Grafana for monitoring
- Jaeger for tracing

# Prerequisites:
- Java 17
- Maven
- Docker (Optional, for containerization observability stack)

# Getting Started:

1. Clone the repository:
   git clone http://github.com/suhasini86/roman-numerals.git
   cd roman-numerals
2. Build the application:
   mvn clean install
3. Run the application:
   mvn spring-boot:run
4. Access the API:

   curl -X "http://localhost:8080/api/roman?number=10"
   example response:
   {
        "input":10,
        "output":"X"
   }

   curl -X "http://localhost:8080/api/roman?number=abc"
    example response:
    {
        "error":"Invalid Input",
        "output":"Query must be a valid integer"
    }

# Testing:
- Run unit tests:
  mvn test
- Run integration tests:
  mvn verify
- Run load tests:
   mvn  -Dtest=RomanNumeralApiLoadTest test
- Test coverage report will be generated in target/site/jacoco/index.html

# Observability:
- Logs are available in the console output.

# Health endpoint
curl http://localhost:8080/actuator/health

# Metrics endpoint
curl http://localhost:8080/actuator/metrics

# Prometheus endpoint
curl http://localhost:8080/actuator/prometheus

# Prometheus Scraping
- Interval: 10 seconds
- Timeout: 5 seconds
- Metrics path: `/actuator/prometheus`

# Grafana Dashboards

1. Application Metrics Dashboard
2. HTTP Request Analytics Dashboard

#Dockerization:
1. Build the Docker image:
   docker build -t roman-numerals:latest .
2. Run the Docker container:
   docker run -p 8080:8080 roman-numerals:latest

## Future Enhancements:
- Extend input range to 39999
- Add range query support with parallel processing.
- Enhance error handling with more specific exceptions.
- Expand CI/CD pipeline for automated testing and deployment.
