# Habit Tracker Module - CRUD Implementation

## Overview
This document provides a complete overview of the Habit Tracker module implementation, following the established architecture patterns of the Personal Manager application.

## Architecture

The module follows a **layered architecture** with clear separation of concerns:

```
habit/
├── application/          # Application layer (Controllers, DTOs, Mappers, Use Cases)
│   ├── HabitController.java
│   ├── HabitLogController.java
│   ├── dto/
│   │   ├── HabitDTO.java
│   │   └── HabitLogDTO.java
│   ├── mapper/
│   │   ├── HabitMapper.java
│   │   └── HabitLogMapper.java
│   └── service/
│       ├── HabitUseCaseService.java
│       └── HabitLogUseCaseService.java
├── domain/              # Domain layer (Entities, Domain Services)
│   ├── model/
│   │   ├── Habit.java
│   │   └── HabitLog.java
│   └── service/
│       └── HabitService.java
├── infrastructure/      # Infrastructure layer (Repositories)
│   └── repository/
│       ├── HabitRepository.java
│       └── HabitLogRepository.java
└── HabitModule.java    # Spring Modulith module marker
```

## Entities

### Habit Entity
```java
@Entity
@Table(name = "habit")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String title;
    private String description;
    
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created_at;
    
    @Temporal(TemporalType.DATE)
    @LastModifiedDate
    private LocalDate last_modified;
    
    @Version
    private Long version;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @OneToMany(mappedBy = "habit")
    private List<HabitLog> logs;
}
```

### HabitLog Entity
```java
@Entity
@Table(name = "habit_log")
public class HabitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created_at;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;
}
```

## API Endpoints

### Habit Endpoints

#### Get All Habits
```http
GET /v1/habits
Authorization: Bearer <token>

Response: 200 OK
[
    {
        "id": 1,
        "title": "Exercise Daily",
        "description": "Do 30 minutes of cardio",
        "createdAt": "2025-12-01",
        "lastModified": "2025-12-01"
    }
]
```

#### Create Habit
```http
POST /v1/habits
Authorization: Bearer <token>
Content-Type: application/json

{
    "title": "Read Books",
    "description": "Read at least 20 pages per day"
}

Response: 200 OK
1  // Returns the ID of created habit
```

#### Update Habit
```http
PUT /v1/habits/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
    "title": "Read Books",
    "description": "Read at least 30 pages per day"
}

Response: 200 OK
```

#### Delete Habit
```http
DELETE /v1/habits/{id}
Authorization: Bearer <token>

Response: 200 OK
```

### HabitLog Endpoints

#### Get All Logs for a Habit
```http
GET /v1/habits/{habitId}/logs
Authorization: Bearer <token>

Response: 200 OK
[
    {
        "id": 1,
        "habitId": 1,
        "createdAt": "2025-12-01"
    },
    {
        "id": 2,
        "habitId": 1,
        "createdAt": "2025-12-02"
    }
]
```

#### Create Habit Log
```http
POST /v1/habits/{habitId}/logs
Authorization: Bearer <token>
Content-Type: application/json

{
    "createdAt": "2025-12-01"
}

Response: 200 OK
1  // Returns the ID of created log
```

#### Delete Habit Log
```http
DELETE /v1/habits/{habitId}/logs/{logId}
Authorization: Bearer <token>

Response: 200 OK
```

## Security

All endpoints are secured with Spring Security and require authentication:
- User authentication is handled via `@AuthenticationPrincipal AppUserPrincipal`
- Each operation verifies that the user owns the resource being accessed
- HabitLog operations additionally verify that the parent Habit belongs to the user

## Key Features

### 1. Multi-tenancy
- All habits are scoped to the authenticated user
- Repository queries filter by `userId` to ensure data isolation
- No user can access another user's habits or logs

### 2. Optimistic Locking
- The `Habit` entity uses `@Version` for optimistic locking
- Prevents concurrent update conflicts

### 3. Audit Trail
- Both entities include `@CreatedDate` for tracking creation time
- `Habit` also includes `@LastModifiedDate` for tracking updates
- Uses Spring Data JPA's auditing feature

### 4. Lazy Loading
- All relationships use `FetchType.LAZY` for performance
- Entity references are used where possible to avoid unnecessary database hits

### 5. Clean Separation of Concerns
- **Controllers**: Handle HTTP requests/responses
- **Use Case Services**: Orchestrate business operations
- **Domain Services**: Contain business logic
- **Repositories**: Handle data persistence
- **Mappers**: Convert between entities and DTOs

## Usage Examples

### Creating a Habit and Logging Progress

```bash
# 1. Create a habit
curl -X POST http://localhost:8080/v1/habits \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Meditation",
    "description": "Meditate for 10 minutes daily"
  }'
# Response: 1

# 2. Log progress (first day)
curl -X POST http://localhost:8080/v1/habits/1/logs \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "createdAt": "2025-12-01"
  }'

# 3. Log progress (second day)
curl -X POST http://localhost:8080/v1/habits/1/logs \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "createdAt": "2025-12-02"
  }'

# 4. View all logs
curl -X GET http://localhost:8080/v1/habits/1/logs \
  -H "Authorization: Bearer <token>"
```

### Updating a Habit

```bash
curl -X PUT http://localhost:8080/v1/habits/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Meditation",
    "description": "Meditate for 15 minutes daily"
  }'
```

## Database Schema

### habit table
```sql
CREATE TABLE habit (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATE,
    last_modified DATE,
    version BIGINT,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_habit_title ON habit(title);
```

### habit_log table
```sql
CREATE TABLE habit_log (
    id BIGSERIAL PRIMARY KEY,
    created_at DATE,
    habit_id BIGINT NOT NULL,
    FOREIGN KEY (habit_id) REFERENCES habit(id) ON DELETE CASCADE
);

CREATE INDEX idx_habit_log_habit_id ON habit_log(habit_id);
CREATE INDEX idx_habit_log_created_at ON habit_log(created_at);
```

## Testing

To test the implementation, you can run:

```bash
# Run all tests
./mvnw test

# Run only habit-related tests (once created)
./mvnw test -Dtest=*Habit*
```

## Next Steps

Consider implementing:
1. **Unit Tests**: Create tests for controllers, services, and domain logic
2. **Integration Tests**: Test the full flow with database
3. **Streak Calculation**: Add a service method to calculate current streak
4. **Statistics**: Add endpoints to get habit statistics (completion rate, longest streak, etc.)
5. **Reminders**: Integrate with a notification system
6. **Categories**: Add habit categories for better organization
7. **Goals**: Set target frequencies (daily, weekly, custom)

## Dependencies

This module depends on:
- `user` module (for `AppUser` and `AppUserPrincipal`)
- Spring Boot Starter Data JPA
- Spring Boot Starter Web
- Spring Boot Starter Security
- MapStruct (for DTO mapping)
- Lombok (for reducing boilerplate)
- PostgreSQL (database)
