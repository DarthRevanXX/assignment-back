# Task Manager Backend

A production-ready REST API for task management, built with Kotlin and Quarkus framework. Features JWT authentication, clean architecture, and comprehensive testing.

## Features

- **JWT Authentication**: Secure token-based authentication with RSA keys
- **Task Management**: Full CRUD operations for tasks
- **Advanced Filtering**: Filter by status, search by keyword, sort by date
- **Pagination**: Efficient pagination (1-100 items per page)
- **Clean Architecture**: Domain, Application, and Infrastructure layers
- **OpenAPI/Swagger**: Interactive API documentation
- **Health Checks**: Readiness and liveness probes
- **Metrics**: Prometheus-compatible metrics
- **Structured Logging**: JSON logs in production
- **Request Tracing**: Correlation ID for request tracking
- **Comprehensive Tests**: 32 tests with MockK and REST Assured
- **Code Quality**: ktlint enforced

## Tech Stack

- **Framework**: Quarkus 3.x
- **Language**: Kotlin 2.2.21
- **Java**: OpenJDK 21 (LTS)
- **Build**: Gradle with Kotlin DSL
- **Security**: SmallRye JWT (RS256)
- **Validation**: Hibernate Validator
- **Testing**: JUnit 5, MockK, REST Assured, AssertJ
- **API Docs**: SmallRye OpenAPI
- **Monitoring**: Micrometer + Prometheus

## Quick Start

### Prerequisites

- Java 21 or higher
- Gradle (wrapper included)

### Installation

```bash
# Clone the repository
git clone <repository-url>
cd task-manager-backend

# Run tests
./gradlew test

# Start development server
./gradlew quarkusDev
```

### Access the Application

- **API Base**: http://localhost:8080
- **Dev UI**: http://localhost:8080/q/dev/
- **Swagger UI**: http://localhost:8080/q/swagger-ui/
- **Health Checks**: http://localhost:8080/q/health
- **Metrics**: http://localhost:8080/q/metrics

## Authentication

### Test Users

Development mode includes two test users:
- Username: `serhii`, Password: `password`
- Username: `bagdan`, Password: `password`

### Login Example

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"serhii","password":"password"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJSUzI1NiIs...",
  "expiresInSeconds": 7200
}
```

## API Endpoints

### Authentication

- `POST /api/v1/auth/login` - User authentication

### Tasks (JWT required)

- `GET /api/v1/tasks` - List tasks with filters
  - Query params: `page`, `size`, `status`, `q` (search), `sort`
- `POST /api/v1/tasks` - Create a new task
- `GET /api/v1/tasks/{id}` - Get task by ID
- `PUT /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task

### Example: List Tasks

```bash
# Use token from login response
curl http://localhost:8080/api/v1/tasks?page=1&size=20&status=PENDING \
  -H "Authorization: Bearer <token>"
```

### Example: Create Task

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Buy groceries",
    "description": "Milk, eggs, bread"
  }'
```

## Development

### Run with Live Reload

```bash
./gradlew quarkusDev
```

Features:
- Automatic code reload on file changes
- CORS enabled for frontend (`http://localhost:3000`)
- Formatted console logs with request IDs
- Dev UI at `/q/dev/`

### Run Tests

```bash
# All tests
./gradlew test

# Specific test
./gradlew test --tests "LoginUseCaseTest"

# Integration tests only
./gradlew test --tests "*IT"

# With coverage
./gradlew test jacocoTestReport
```

### Code Formatting

```bash
# Check code style
./gradlew ktlintCheck

# Auto-format
./gradlew ktlintFormat
```

## Docker Deployment

### Using Docker Compose (Recommended)

```bash
# Build and run
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

Backend will be available at:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health

### Manual Docker Build

```bash
# Build image
docker build -t task-manager-backend .

# Run container
docker run -p 8080:8080 task-manager-backend
```

## Building for Production

### Standard JAR

```bash
# Build
./gradlew build

# Run
java -jar build/quarkus-app/quarkus-run.jar
```

### Uber JAR (single file)

```bash
# Build
./gradlew build -Dquarkus.package.jar.type=uber-jar

# Run
java -jar build/task-manager-backend-1.0.0-SNAPSHOT-runner.jar
```

### Native Executable (GraalVM)

```bash
# Build native (requires GraalVM installed)
./gradlew build -Dquarkus.native.enabled=true

# Or build in container (no GraalVM required)
./gradlew build -Dquarkus.native.enabled=true \
  -Dquarkus.native.container-build=true

# Run native executable (~8ms startup!)
./build/task-manager-backend-1.0.0-SNAPSHOT-runner
```

## Configuration

Configuration in `src/main/resources/application.properties`:

### Key Settings

- **Port**: `8080`
- **JWT Expiration**: 120 minutes (2 hours)
- **CORS**: Enabled in dev mode for `http://localhost:3000`
- **Logging**: JSON format in production, formatted in dev
- **Health Checks**: `/q/health` (readiness and liveness)
- **OpenAPI**: `/q/openapi`, Swagger UI at `/q/swagger-ui`

### Profiles

- **dev** (default): Development mode with CORS and formatted logs
- **prod**: Production mode with JSON logs and stricter security

## Monitoring

### Health Checks

```bash
# Readiness (can serve traffic?)
curl http://localhost:8080/q/health/ready

# Liveness (is app running?)
curl http://localhost:8080/q/health/live

# All checks
curl http://localhost:8080/q/health
```

### Metrics

Prometheus-compatible metrics:

```bash
curl http://localhost:8080/q/metrics
```

Includes:
- HTTP request counters and timers
- JVM metrics (memory, threads, GC)
- Custom business metrics

### Logging

**Development**: Formatted console logs
```
14:23:45.123 INFO  [c.s.t.a.u.LoginUseCase] (executor-thread-1) req=abc123 - Login attempt: serhii
```

**Production**: JSON structured logs
```json
{
  "timestamp": "2025-01-01T14:23:45.123Z",
  "level": "INFO",
  "logger": "com.serhii.taskmanager.application.usecase.LoginUseCase",
  "message": "Login attempt: serhii",
  "requestId": "abc123"
}
```

## Architecture

### Clean Architecture Layers

```
domain/          # Business logic (entities, interfaces)
  ├── model/     # Task, User, enums
  ├── repo/      # Repository interfaces
  └── service/   # Domain services

application/     # Application logic (use cases)
  ├── dto/       # Data Transfer Objects
  └── usecase/   # Use case implementations

infrastructure/  # Technical implementation
  ├── http/      # HTTP filters, headers
  ├── obs/       # Observability (correlation ID)
  ├── persistence/ # In-memory repository
  ├── security/  # JWT, authentication
  ├── web/       # REST resources
  └── health/    # Health checks

common/          # Shared utilities
```

### Key Principles

- **Dependency Rule**: Dependencies point inward (infrastructure → application → domain)
- **Use Case Pattern**: Each operation is a separate use case
- **Repository Pattern**: Abstract data access behind interfaces
- **Domain Services**: Encapsulate business rules
- **Exception Mapping**: Global exception handlers for consistent errors

## Testing

### Test Coverage

**32 tests** across:
- **Use Case Tests** (15): Business logic tests
- **Domain Service Tests** (6): Business rules validation
- **Infrastructure Tests** (2): Identity provider
- **Integration Tests** (9): Full HTTP request/response cycle

### Test Tools

- **JUnit 5**: Test framework
- **MockK**: Kotlin mocking library
- **REST Assured**: HTTP API testing
- **AssertJ**: Fluent assertions
- **Quarkus Test**: Resource lifecycle management

## Security

### JWT Authentication

- **Algorithm**: RS256 (RSA with SHA-256)
- **Token Expiration**: 2 hours (configurable)
- **Keys**: RSA key pair (2048-bit)
  - **Generated at startup** in `/tmp/task-manager-keys/`
  - Private key: `privateKey.pem` (signing)
  - Public key: `publicKey.pem` (verification)
  - **Security**: Keys are NOT committed to repository
  - In Docker: Keys regenerated on each container start
  - In production: Use Vault, K8s secrets, or AWS Secrets Manager
- **Issuer**: `task-manager`
- **Audience**: `task-manager-web`

### Authorization

- **Role-Based**: `@RolesAllowed("USER")` on endpoints
- **Owner Verification**: Users can only access their own tasks
- **Default Deny**: All endpoints denied by default

### Security Best Practices

- ✅ JWT with strong encryption (RSA-256)
- ✅ **Runtime key generation** - No hardcoded private keys
- ✅ Token expiration and refresh logic ready
- ✅ Role-based access control
- ✅ Owner-based resource access
- ✅ Security headers configured
- ✅ CORS properly configured per environment
- ✅ No secrets in code

## Project Structure

```
task-manager-backend/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/serhii/taskmanager/
│   │   │       ├── domain/
│   │   │       ├── application/
│   │   │       ├── infrastructure/
│   │   │       ├── common/
│   │   │       └── quarkus/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── kotlin/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── .gitignore
├── .editorconfig
└── README.md
```

## Environment Variables

No environment variables required for development. For production:

```bash
# Optional: Override default port
export QUARKUS_HTTP_PORT=8080

# Optional: Set profile
export QUARKUS_PROFILE=prod
```

## Troubleshooting

### Port Already in Use

```bash
# Change port
./gradlew quarkusDev -Dquarkus.http.port=8081
```

### Tests Failing

```bash
# Clean build and retry
./gradlew clean test
```

### Native Build Issues

Ensure GraalVM is installed or use container build:
```bash
./gradlew build -Dquarkus.native.enabled=true \
  -Dquarkus.native.container-build=true
```

## Additional Documentation

- **Swagger UI** - Interactive API documentation at `/q/swagger-ui`
- **Dev UI** - Quarkus development tools at `/q/dev`

## Known Limitations

1. **In-Memory Storage**: Data doesn't persist between restarts (per requirements)
2. **Simple Authentication**: 2 hardcoded test users (demo purposes only)
3. **Ephemeral JWT Keys**: Keys regenerated on restart (demo mode - use secrets manager in production)
4. **No Rate Limiting**: Would be added with Quarkus extensions in production
5. **Single Instance**: No distributed features (acceptable for assessment)

## Production Deployment

This application is production-ready with:
- ✅ Comprehensive tests passing
- ✅ Health checks for Kubernetes/Docker
- ✅ Prometheus metrics for monitoring
- ✅ Structured JSON logging
- ✅ Security best practices
- ✅ Clean architecture
- ✅ OpenAPI documentation

For real production:
1. Replace `SimpleIdentityProvider` with OIDC (Keycloak, Auth0)
2. Add database (PostgreSQL, MongoDB)
3. Add Redis for caching
4. Configure load balancer
5. Set up CI/CD pipeline
6. Add distributed tracing (Jaeger, Zipkin)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Run tests: `./gradlew test`
4. Format code: `./gradlew ktlintFormat`
5. Commit changes
6. Push and create Pull Request

## License

This project is part of a technical assessment.

## Support

For issues or questions, please open an issue in the repository.
