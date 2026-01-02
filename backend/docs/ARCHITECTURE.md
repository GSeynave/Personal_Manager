# Project Architecture

## System Overview

<!-- Brief description of what your application does -->
**Personal Growth Hub** - A comprehensive platform helping users elevate themselves through structured tracking of productivity, habits, health, and personal development, wrapped in a gamified social experience.

## Vision & Philosophy

**Core Purpose:** Help users "pause" in a fast-paced world by providing a space to unload their mental burden, track their growth, and celebrate progress.

**Key Principles:**
- **Spiritual Growth over Task Completion** - Focus on becoming, not just doing
- **Gamification for Motivation** - Dopamine rewards that drive real elevation
- **Social Pride without Competition** - Share achievements, support friends
- **One Step at a Time** - Small daily actions compound into transformation

## Architecture Style

**Type:** Multi-module Monolith (with potential for microservices evolution)

**Why Multi-Module?**
- Clear separation of concerns by domain
- Independent development and testing per module
- Shared core infrastructure
- Easier to extract to microservices later if needed

## High-Level Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                         Frontend                                  │
│                  Vue 3 + shadcn-vue + Tailwind                    │
└────────────────────────┬──────────────────────────────────────────┘
                         │ REST API
┌────────────────────────▼──────────────────────────────────────────┐
│                    API Gateway / Controller Layer                 │
│                     (Spring Boot REST)                            │
└──────────────────────────────┬────────────────────────────────────┘
                               │
        ┌──────────────────────┼────────────────┐
        │                      │                │
┌───────▼────────────────┐ ┌────▼─────┐ ┌───────▼────────┐
│ Core Domains           │ │ Cross-   │ │ Infrastructure │
│                        │ │ Cutting  │ │                │
│ • Productivity & Focus │ │ Layers   │ │ • Auth         │
│ • Wellness & Helath    │ │          │ │ • Security     │
│ • Finance & Resources  │ │ • Game   │ │ • Events       │
│ • Hobbies              │ │ • Social │ │ • Config       │
│ • Social               │ │ • AI     │ │ • Metrics      │
└────────────────────────┘ └──────────┘ └────────────────┘
        │                        │                │
        └────────────────────────┼────────────────┘
                                 │
                        ┌────────▼─────────┐
                        │   PostgreSQL     │
                        │   Database       │
                        └──────────────────┘
```

## Module Structure

### Core Modules (Infrastructure)

**1. core-module**
- **Purpose:** Shared entities, utilities, base classes
- **Responsibilities:**
  - User entity and authentication
  - Common DTOs and mappers
  - Utility classes
  - Base repository/service classes
  - Exception handling
- **Dependencies:** None (foundation)

**2. security-module** (if separated)
- **Purpose:** Authentication and authorization
- **Responsibilities:**
  - JWT token generation/validation
  - Spring Security configuration
  - User authentication
  - Permission management
- **Dependencies:** core-module

### Domain Modules (Business Logic)

**3. todo-module** (Productivity & Focus)
- **Purpose:** Task and project management
- **Responsibilities:**
  - Task CRUD operations
  - Task groups and categorization
  - Priority management
  - Due date tracking
  - XP reward integration
- **Key Entities:** Task, TaskGroup, Project
- **Events Published:** TaskCompletedEvent, TaskCreatedEvent
- **Dependencies:** core-module

**4. habits-module** (Health & Wellness Foundation)
- **Purpose:** Recurring behavior tracking
- **Responsibilities:**
  - Habit creation and tracking
  - Daily check-ins
  - Streak calculation
  - Frequency management (daily, weekly, custom)
  - XP reward integration
- **Key Entities:** Habit, HabitEntry, Streak
- **Events Published:** HabitCompletedEvent, StreakAchievedEvent
- **Dependencies:** core-module

**5. finance-module** (Future)
- **Purpose:** Financial tracking and management
- **Responsibilities:**
  - Accounting (income, expenses)
  - Subscription tracking
  - Debt management
  - Budget planning
- **Key Entities:** Transaction, Subscription, Debt, Budget
- **Events Published:** TransactionCreatedEvent, BudgetExceededEvent
- **Dependencies:** core-module

**6. health-module** (Future)
- **Purpose:** Physical and mental health tracking
- **Responsibilities:**
  - Diet/nutrition logging
  - Sleep tracking
  - Fitness activities
  - Health metrics
- **Key Entities:** Meal, SleepEntry, Workout, HealthMetric
- **Events Published:** WorkoutCompletedEvent, HealthGoalAchievedEvent
- **Dependencies:** core-module

**7. learning-module** (Future)
- **Purpose:** Personal development and skill tracking
- **Responsibilities:**
  - Course tracking
  - Skill development
  - Reading lists
  - Learning goals
- **Key Entities:** Course, Skill, Book, LearningGoal
- **Events Published:** CourseCompletedEvent, SkillLeveledUpEvent
- **Dependencies:** core-module

### Cross-Cutting Modules (Horizontal Concerns)

**8. gamification-module**
- **Purpose:** XP, levels, achievements, badges system
- **Responsibilities:**
  - Listen to domain events (TaskCompleted, HabitCompleted, etc.)
  - Award XP based on actions
  - Level calculation
  - Badge/achievement unlocking
  - Streak tracking (cross-domain)
  - Title/border management
- **Key Entities:** GameProfile, Achievement, Badge, Title, XPTransaction
- **Events Consumed:** All *CompletedEvent from domain modules
- **Events Published:** LevelUpEvent, AchievementUnlockedEvent
- **Dependencies:** core-module + READ access to domain modules (queries only, no writes)

**9. social-module**
- **Purpose:** Friend connections, sharing, and social features
- **Responsibilities:**
  - Friend system (add, remove, manage)
  - Achievement sharing
  - Pacts/shared goals (contracts between friends)
  - Social feed
  - Leaderboards (opt-in)
  - Group challenges
- **Key Entities:** Friendship, Pact, SocialPost, Challenge, Group
- **Events Consumed:** AchievementUnlockedEvent, LevelUpEvent
- **Events Published:** PactCompletedEvent, FriendInvitedEvent
- **Dependencies:** core-module, gamification-module (read)

**10. analytics-module** (Future)
- **Purpose:** Insights, patterns, reporting
- **Responsibilities:**
  - User behavior analysis
  - Pattern detection (e.g., productivity drops when sleep is poor)
  - Weekly/monthly reports
  - Goal progress tracking
  - Predictive insights
- **Key Entities:** UserInsight, Report, Pattern
- **Events Consumed:** All domain events
- **Dependencies:** core-module + READ access to all modules

**11. ai-module** (Future)
- **Purpose:** AI-powered features
- **Responsibilities:**
  - Task priority suggestions
  - Habit recommendations
  - Personalized insights
  - Smart goal setting
  - Conversational interface
- **Dependencies:** core-module + access to domain modules

## Module Dependency Rules

**CRITICAL RULES:**

1. **Core modules NEVER depend on feature modules**
   - ✅ todo-module → core-module
   - ❌ core-module → todo-module

2. **Domain modules NEVER depend on each other directly**
   - ✅ Communication via events only
   - ❌ todo-module → habits-module (direct dependency)

3. **Cross-cutting modules can READ from domains**
   - ✅ gamification-module → queries domain data
   - ❌ gamification-module → modifies domain entities directly

4. **Event-driven communication for cross-module interactions**
   - Domain publishes events
   - Cross-cutting layers subscribe and react
   - Decoupled, scalable, testable

## Module Dependency Graph

```
                    core-module
                         ↓
    ┌────────────────────┼────────────────────┐
    ↓                    ↓                    ↓
todo-module      habits-module      finance-module
    │                    │                    │
    └────────────────────┼────────────────────┘
                         ↓ (events)
                 gamification-module
                         ↓
                  social-module
```

## Communication Patterns

### 1. REST API (External)
- Frontend ↔ Backend
- Standard REST conventions
- JSON request/response
- JWT authentication

### 2. Domain Events (Internal)
- Asynchronous communication between modules
- Spring ApplicationEventPublisher
- Loose coupling
- Examples:
  - `TaskCompletedEvent` → gamification-module awards XP
  - `HabitCompletedEvent` → gamification-module updates streak
  - `LevelUpEvent` → social-module notifies friends

### 3. Direct Queries (Read-Only)
- Cross-cutting modules can query domain data
- READ ONLY (no writes to other module's tables)
- Example: gamification-module queries tasks to calculate productivity score

## Data Storage Strategy

**Single Database (PostgreSQL)** with schema separation:

```
Schemas:
- core_schema      (users, auth)
- todo_schema      (tasks, groups)
- habits_schema    (habits, entries)
- game_schema      (profiles, achievements)
- social_schema    (friendships, pacts)
```

**Why single DB?**
- Simpler for MVP
- ACID transactions when needed
- Easier queries across domains (for analytics)
- Can split to microservices later

**Future consideration:** If scaling becomes an issue, can separate by:
- Read replicas for analytics
- Separate DBs per bounded context
- Event sourcing for audit trail

## Key Technology Decisions

### Why Spring Boot?
- Mature ecosystem
- Excellent multi-module support
- Strong dependency injection
- Built-in security, data access, REST support
- Easy event handling (ApplicationEventPublisher)

### Why PostgreSQL?
- Relational data (complex queries for leaderboards, analytics)
- JSONB support for flexible data (future: user preferences)
- Excellent performance
- Strong consistency guarantees

### Why Event-Driven Architecture?
- Decouples modules
- Easy to add new features (just subscribe to events)
- Supports async processing
- Natural fit for gamification triggers

### Why Multi-Module Monolith (not Microservices)?
**For now:**
- Simpler deployment
- Easier development (no distributed system complexity)
- Faster iteration
- Sufficient for MVP scale

**Future path to microservices:**
- Well-defined module boundaries make extraction easy
- Event-driven already in place
- Can split when scaling demands it

## Deployment Architecture

**Current (MVP):**
```
┌─────────────────────────────────────┐
│         Docker Container            │
│                                     │
│  ┌──────────────────────────────┐   │
│  │   Spring Boot App            │   │
│  │   (All modules)              │   │
│  └──────────────────────────────┘   │
│                                     │
│  ┌──────────────────────────────┐   │
│  │   PostgreSQL                 │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
         ↓
    Cloud Provider
    (Heroku, Railway, Render)
```

**Future (if scaling):**
```
Load Balancer
    ↓
┌───┴───┐
│  API  │  (Stateless, horizontal scaling)
└───┬───┘
    ↓
┌───┴───┐
│Modules│  (Can extract to separate services)
└───┬───┘
    ↓
PostgreSQL (managed, replicas)
```

## Security Considerations

- JWT tokens for authentication
- Role-based access control (RBAC)
- User can only access their own data (enforced at repository level)
- Friend data visible only if friendship exists
- API rate limiting (future)
- Input validation on all endpoints

## Performance Considerations

- Database indexes on frequently queried fields (user_id, created_at)
- Caching for gamification calculations (Redis, future)
- Async event processing for non-critical paths
- Pagination for list endpoints
- Database connection pooling

## Testing Strategy

- **Unit tests:** Each module independently (JUnit 5, Mockito)
- **Integration tests:** Module interactions via events (TestContainers)
- **API tests:** End-to-end REST endpoint testing (RestAssured)
- **Performance tests:** Load testing critical paths (JMeter, future)

## Monitoring & Observability (Future)

- Spring Boot Actuator for health checks
- Prometheus + Grafana for metrics
- Sentry for error tracking
- Structured logging (JSON logs)
- Distributed tracing (if splitting to microservices)

## Evolution Strategy

**Phase 1 (MVP):** Core + Productivity + Habits + Gamification + Social (basic)
**Phase 2:** Add Finance + Health modules
**Phase 3:** Add Learning + Analytics modules
**Phase 4:** Add AI-powered features
**Phase 5:** Consider microservices if scale demands

---

## Decision Log

### Why not microservices from the start?
**Decision:** Start with multi-module monolith
**Reason:** Faster development, simpler deployment, sufficient for MVP. Event-driven design makes future extraction easy.
**Date:** 06-12-2025

### Why PostgreSQL over MongoDB?
**Decision:** PostgreSQL
**Reason:** Relational queries for leaderboards, analytics, friend connections. Strong consistency. JSONB for flexibility where needed.
**Date:** 06-12-2025

### Why event-driven communication?
**Decision:** Spring ApplicationEventPublisher for inter-module communication
**Reason:** Decouples modules, makes gamification reactive, supports async processing, easy to test.
**Date:** 06-12-2025

---

**Last Updated:** [06-12-2025]
**Maintained By:** [Gauthier Seynave]
