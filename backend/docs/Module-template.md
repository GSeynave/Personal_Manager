# [Module Name] Module

> **Quick Summary:** [One-sentence description of what this module does]

## Purpose

<!-- Why does this module exist? What problem does it solve? -->

[Detailed description of the module's role in the system]

## Responsibilities

<!-- What is this module responsible for? -->

- [ ] Responsibility 1
- [ ] Responsibility 2
- [ ] Responsibility 3

## Module Type

- [ ] **Core Module** (Infrastructure/Foundation)
- [ ] **Domain Module** (Business Logic)
- [ ] **Cross-Cutting Module** (Horizontal Concern)

## Package Structure

```
[module-name]/
├── src/
│   ├── main/
│   │   ├── java/com/yourapp/[module]/
│   │   │   ├── controller/        # REST endpoints
│   │   │   │   └── [Entity]Controller.java
│   │   │   ├── service/           # Business logic
│   │   │   │   ├── [Entity]Service.java
│   │   │   │   └── impl/
│   │   │   │       └── [Entity]ServiceImpl.java
│   │   │   ├── repository/        # Data access
│   │   │   │   └── [Entity]Repository.java
│   │   │   ├── entity/            # JPA entities
│   │   │   │   ├── [Entity].java
│   │   │   │   └── [RelatedEntity].java
│   │   │   ├── dto/               # Data transfer objects
│   │   │   │   ├── request/
│   │   │   │   │   ├── Create[Entity]RequestDTO.java
│   │   │   │   │   └── Update[Entity]RequestDTO.java
│   │   │   │   └── response/
│   │   │   │       └── [Entity]ResponseDTO.java
│   │   │   ├── mapper/            # Entity ↔ DTO conversion
│   │   │   │   └── [Entity]Mapper.java
│   │   │   ├── event/             # Domain events
│   │   │   │   ├── [Entity]CreatedEvent.java
│   │   │   │   └── [Entity]CompletedEvent.java
│   │   │   ├── listener/          # Event listeners
│   │   │   │   └── [Entity]EventListener.java
│   │   │   ├── exception/         # Custom exceptions
│   │   │   │   ├── [Entity]NotFoundException.java
│   │   │   │   └── [Entity]ValidationException.java
│   │   │   ├── config/            # Module configuration
│   │   │   │   └── [Module]Config.java
│   │   │   └── util/              # Utility classes
│   │   │       └── [Module]Utils.java
│   │   └── resources/
│   │       ├── db/migration/      # Flyway migrations
│   │       │   └── V1__create_[table].sql
│   │       └── application-[module].yml
│   └── test/
│       └── java/com/yourapp/[module]/
│           ├── controller/        # Controller tests
│           ├── service/           # Service tests
│           ├── repository/        # Repository tests
│           └── integration/       # Integration tests
├── pom.xml
└── README.md
```

## Entities

### [Primary Entity Name]

**Purpose:** [What this entity represents]

**Fields:**
```java
@Entity
@Table(name = "[table_name]")
public class [EntityName] {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID userId;  // FK to User
    
    @Column(nullable = false)
    private String [field1];
    
    @Column
    private String [field2];
    
    @Enumerated(EnumType.STRING)
    private [Status]Enum status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "related_id")
    private RelatedEntity related;
    
    @OneToMany(mappedBy = "parent")
    private List<ChildEntity> children;
}
```

**Relationships:**
- **Belongs to:** User (many-to-one)
- **Has many:** ChildEntity (one-to-many)
- **Associated with:** RelatedEntity (many-to-one)

**Business Rules:**
- [ ] Rule 1: [Description]
- [ ] Rule 2: [Description]

### [Secondary Entity Name] (if applicable)

**Purpose:** [What this entity represents]

**Fields:**
```java
// Similar structure
```

## Database Schema

**Tables:**
- `[table_1]` - [Description]
- `[table_2]` - [Description]

**Key Indexes:**
```sql
CREATE INDEX idx_[table]_user_id ON [table](user_id);
CREATE INDEX idx_[table]_created_at ON [table](created_at);
CREATE INDEX idx_[table]_status ON [table](status);
```

**Foreign Keys:**
- `user_id` → `users.id` (ON DELETE CASCADE)
- `[related]_id` → `[related_table].id`

## API Endpoints

### [Entity] Management

#### Create [Entity]
```http
POST /api/v1/[entities]
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "field1": "value",
  "field2": "value"
}

Response: 201 Created
{
  "id": "uuid",
  "field1": "value",
  "field2": "value",
  "userId": "uuid",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### List [Entities]
```http
GET /api/v1/[entities]?page=0&size=20&sort=createdAt,desc
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

#### Get [Entity]
```http
GET /api/v1/[entities]/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "id": "uuid",
  "field1": "value",
  ...
}

Error: 404 Not Found (if entity doesn't exist or doesn't belong to user)
```

#### Update [Entity]
```http
PUT /api/v1/[entities]/{id}
Content-Type: application/json
Authorization: Bearer {token}

Request Body:
{
  "field1": "new value"
}

Response: 200 OK
{
  "id": "uuid",
  "field1": "new value",
  ...
}
```

#### Delete [Entity]
```http
DELETE /api/v1/[entities]/{id}
Authorization: Bearer {token}

Response: 204 No Content
```

#### [Custom Action]
```http
POST /api/v1/[entities]/{id}/[action]
Authorization: Bearer {token}

Response: 200 OK
{
  "message": "Action completed",
  "result": {...}
}
```

## Events

### Events Published

#### [Entity]CreatedEvent
**Trigger:** When a new [entity] is created
**Payload:**
```java
{
  UUID entityId;
  UUID userId;
  LocalDateTime createdAt;
}
```
**Consumers:** analytics-module (for metrics)

#### [Entity]CompletedEvent
**Trigger:** When [entity] is marked as completed
**Payload:**
```java
{
  UUID entityId;
  UUID userId;
  int xpReward;
  LocalDateTime completedAt;
}
```
**Consumers:** gamification-module (awards XP), social-module (may notify friends)

#### [Entity]DeletedEvent
**Trigger:** When [entity] is deleted
**Payload:**
```java
{
  UUID entityId;
  UUID userId;
}
```
**Consumers:** analytics-module (for metrics)

### Events Consumed

#### [ExternalEvent]
**Source:** [other-module]
**Purpose:** [Why this module listens to this event]
**Handler:** `[Entity]EventListener.handle[Event]()`
**Action:** [What happens when event is received]

## Dependencies

### Module Dependencies
```xml
<!-- Parent -->
<dependency>
    <groupId>com.yourapp</groupId>
    <artifactId>core-module</artifactId>
</dependency>

<!-- If cross-cutting module, read-only access to domains -->
<!-- No direct dependencies, use queries only -->
```

### External Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<!-- Add module-specific dependencies -->
```

### Dependency Rules
- ✅ Can depend on: core-module
- ❌ Cannot depend on: other domain modules (use events)
- ✅ Can query: [if cross-cutting] read-only access to domain data

## Service Layer

### [Entity]Service

**Key Methods:**

```java
public interface [Entity]Service {
    
    /**
     * Creates a new [entity] for the authenticated user
     * @param request The creation request
     * @param userId The authenticated user's ID
     * @return The created entity
     * @throws ValidationException if request is invalid
     */
    [Entity]ResponseDTO create(Create[Entity]RequestDTO request, UUID userId);
    
    /**
     * Retrieves [entity] by ID
     * Only returns if [entity] belongs to the requesting user
     * @param id The [entity] ID
     * @param userId The authenticated user's ID
     * @return The [entity]
     * @throws [Entity]NotFoundException if not found or unauthorized
     */
    [Entity]ResponseDTO getById(UUID id, UUID userId);
    
    /**
     * Lists all [entities] for the authenticated user
     * @param userId The authenticated user's ID
     * @param pageable Pagination parameters
     * @return Page of [entities]
     */
    Page<[Entity]ResponseDTO> listByUser(UUID userId, Pageable pageable);
    
    /**
     * Updates an existing [entity]
     * @param id The [entity] ID
     * @param request The update request
     * @param userId The authenticated user's ID
     * @return The updated [entity]
     * @throws [Entity]NotFoundException if not found or unauthorized
     */
    [Entity]ResponseDTO update(UUID id, Update[Entity]RequestDTO request, UUID userId);
    
    /**
     * Deletes a [entity]
     * Publishes [Entity]DeletedEvent
     * @param id The [entity] ID
     * @param userId The authenticated user's ID
     * @throws [Entity]NotFoundException if not found or unauthorized
     */
    void delete(UUID id, UUID userId);
    
    /**
     * [Custom business logic method]
     * @param id The [entity] ID
     * @param userId The authenticated user's ID
     * @return [Result]
     */
    [Result] [customMethod](UUID id, UUID userId);
}
```

## Validation Rules

### Create[Entity]RequestDTO
```java
@NotBlank(message = "Field1 is required")
@Size(max = 255, message = "Field1 must not exceed 255 characters")
private String field1;

@NotNull(message = "Field2 is required")
@Min(value = 0, message = "Field2 must be positive")
private Integer field2;

@Pattern(regexp = "^[A-Z]{3}$", message = "Field3 must be 3 uppercase letters")
private String field3;
```

### Business Validation
- [ ] User can only access their own [entities]
- [ ] [Custom business rule]
- [ ] [Custom business rule]

## Use Cases

### Use Case 1: [Action Name]

**Actor:** Authenticated User

**Preconditions:**
- User is authenticated
- [Any other preconditions]

**Flow:**
1. User sends request to create/update/delete [entity]
2. System validates request data
3. System checks user authorization (owns the [entity])
4. System performs business logic
5. System persists changes to database
6. System publishes relevant event
7. System returns response to user

**Postconditions:**
- [Entity] is created/updated/deleted
- Event is published for other modules to react
- User receives confirmation

**Edge Cases:**
- What if [entity] doesn't exist?
- What if user doesn't own the [entity]?
- What if [business rule] is violated?

### Use Case 2: [Event Reaction]

**Trigger:** [EventName] received from [source-module]

**Flow:**
1. Listener receives event
2. System fetches relevant [entity] data
3. System performs calculation/update
4. System persists changes
5. (Optional) System publishes new event

**Example:**
When `TaskCompletedEvent` is received:
1. Gamification module calculates XP reward
2. Awards XP to user's GameProfile
3. Checks for level up
4. Publishes `LevelUpEvent` if threshold reached

## Testing

### Unit Tests

**Coverage Target:** >80%

**Key Test Cases:**
- [ ] Service layer logic (mocked repositories)
- [ ] Validation rules
- [ ] Business rule enforcement
- [ ] Event publishing
- [ ] Exception handling

**Example:**
```java
@Test
void create_ValidRequest_Success() {
    // Given
    Create[Entity]RequestDTO request = ...;
    UUID userId = ...;
    
    // When
    [Entity]ResponseDTO response = service.create(request, userId);
    
    // Then
    assertNotNull(response.getId());
    assertEquals(request.getField1(), response.getField1());
    verify(eventPublisher).publishEvent(any([Entity]CreatedEvent.class));
}
```

### Integration Tests

**Uses TestContainers** for real database

**Key Test Cases:**
- [ ] Full request → response flow
- [ ] Event publishing and consumption
- [ ] Transaction boundaries
- [ ] Database constraints

### API Tests

**Uses RestAssured / MockMvc**

**Key Test Cases:**
- [ ] All endpoints (CRUD operations)
- [ ] Authentication/authorization
- [ ] Input validation errors
- [ ] Error responses (404, 400, 403)

## Configuration

### Application Properties
```yaml
# application-[module].yml
[module]:
  feature:
    enabled: true
  [setting]: [value]
```

### Environment Variables
- `[MODULE]_[SETTING]` - [Description]

## Common Issues & Solutions

### Issue 1: [Problem Description]
**Symptom:** [What users/developers see]
**Cause:** [Why it happens]
**Solution:** [How to fix]

### Issue 2: [Problem Description]
**Symptom:** [What users/developers see]
**Cause:** [Why it happens]
**Solution:** [How to fix]

## Future Enhancements

- [ ] Enhancement 1: [Description]
- [ ] Enhancement 2: [Description]
- [ ] Enhancement 3: [Description]

## Related Documentation

- [ARCHITECTURE.md](../docs/ARCHITECTURE.md) - Overall system architecture
- [TECH_STACK.md](../docs/TECH_STACK.md) - Technology decisions
- [[Related Module] README](../[related-module]/README.md) - Related functionality

---

**Module Owner:** [Your Name]
**Last Updated:** [Date]
**Status:** [In Development | Active | Deprecated]
