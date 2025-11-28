# Technical Stack

## Backend

### Core Technology
- **Java**: 21
- **Spring Boot**: 3.4.0
- **Maven**: Build tool
- **Spring Modulith**: 1.2.5

### Frameworks & Libraries
- **Spring Web**: REST API
- **Spring Data JPA**: Database ORM
- **Spring Security**: Authentication/Authorization
- **Spring Cloud OpenFeign**: 2024.0.0 - HTTP client
- **Spring Actuator**: Monitoring

### Database
- **PostgreSQL**: 16-alpine (Production)
- **H2**: In-memory database (Unit tests)

### Utilities
- **Lombok**: 1.18.30 - Code generation
- **MapStruct**: 1.6.3 - Object mapping
- **Auth0 JWT**: 4.4.0 - JWT token handling

### Testing
- **JUnit 5**: Test framework (via Spring Boot Test)
- **Testcontainers**: 1.19.3 - Integration testing
  - PostgreSQL module
  - JUnit Jupiter integration
- **REST Assured**: E2E API testing
- **AssertJ**: Fluent assertions
- **JaCoCo**: 0.8.10 - Code coverage

### Development Tools
- **Spring DevTools**: Hot reload

---

## Frontend

### Core Technology
- **Node.js**: ^20.19.0 || >=22.12.0
- **Vue.js**: 3.5.22
- **TypeScript**: ~5.9.0
- **Vite**: 7.1.11 - Build tool

### UI Framework
- **PrimeVue**: 4.4.1 - Component library
- **PrimeIcons**: 7.0.0 - Icon set
- **PrimeUIX Themes**: 1.2.5 - Theming

### State & Routing
- **Vue Router**: 4.6.3 - Routing
- **Pinia**: 3.0.3 - State management

### Libraries
- **Axios**: 1.13.1 - HTTP client
- **Firebase**: 12.6.0 - Authentication
- **Vue CSV Import**: 4.1.2 - CSV handling

### Development Tools
- **Vue TSC**: 3.1.1 - Type checking
- **ESLint**: 9.37.0 - Linting
- **Prettier**: 3.6.2 - Code formatting
- **Vite DevTools**: 8.0.3 - Debugging
- **npm-run-all2**: 8.0.4 - Script runner

---

## Infrastructure

### Containers
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration

### Database
- **PostgreSQL**: 16-alpine

---

## CI/CD Pipeline

### GitHub Actions Workflows
- **Runner**: ubuntu-latest

### Backend CI
- **actions/checkout**: v4 - Repository checkout
- **actions/setup-java**: v4 - JDK setup
  - Java Distribution: Temurin
  - Maven caching enabled
- **actions/cache**: v3 - Maven dependencies caching
- **actions/upload-artifact**: v4 - Test results & JAR upload
- **codecov/codecov-action**: v4 - Code coverage reporting
- **Maven Checkstyle**: Code style validation

### Frontend CI
- **actions/checkout**: v4 - Repository checkout
- **actions/setup-node**: v4 - Node.js setup
  - NPM caching enabled
- **actions/cache**: v3 - Node modules caching
- **actions/upload-artifact**: v4 - Build artifact upload

### Code Quality
- **aquasecurity/trivy-action**: Security vulnerability scanning
- **trufflesecurity/trufflehog**: Secret detection

### Triggers
- Push to `main` or `dev` branches
- Pull requests to `main` or `dev` branches

---

## Version Control
- **Git**: Version control
- **GitHub**: Repository hosting

---

*Last updated: November 2025*
