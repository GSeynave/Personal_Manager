# Gamification Module

## Overview
Cross-cutting gamification system that rewards user actions across all domains with meaningful progression and cosmetic rewards.

## Architecture

### Event-Driven Design
The gamification module uses Spring's event system to listen for completion events from other modules:
- **TaskCompletedEvent** - Published when a todo is marked complete
- **HabitCompletedEvent** - Published when a habit log is completed
- **LevelUpEvent** - Published when a user gains a level
- **AchievementUnlockedEvent** - Published when a user unlocks an achievement

This ensures loose coupling between modules.

## Core Concepts

### Essence (Progression Currency)
- Users earn **Essence** for completing actions
- Essence accumulates toward **Level progression**
- Base essence values:
  - Task completion: 20 essence
  - Habit completion: 15 essence
  - Streak bonuses: 50-500 essence

### Levels & Titles
Users progress through levels using an exponential curve:
```
Level 1: 0 essence (Freshman)
Level 2: 400 essence (Novice)
Level 3: 900 essence (Apprentice)
Level 4: 1600 essence (Adept)
Level 5: 2500 essence (Journeyman)
...
```

Formula: Required essence = 100 * level²

### Achievements
Triggered by specific accomplishments:
- **MILESTONE** - One-time achievements (e.g., "First Task")
- **CUMULATIVE** - Total count achievements (e.g., "100 Tasks Completed")
- **STREAK** - Consecutive day achievements (e.g., "7-Day Streak")
- **DOMAIN_MASTERY** - Cross-domain achievements (e.g., "Level 5 in 3 domains")

### Rewards (Cosmetic Unlocks)
Achievements unlock customization options:
- **TITLE** - Display name suffix (e.g., "the Apprentice")
- **BORDER** - Profile card border styles
- **EMOJI** - Exclusive emoji sets
- **NAME_FONT** - Typography styles
- **NAME_COLOR** - Color gradients

## Anti-Cheat System

### 1. One-Time Essence Award
Each action can only award essence once via the `essenceAwarded` flag on entities.

### 2. Time-Based Constraints
- Habits use unique constraint on `(habit_id, user_id, completion_date)`
- Prevents multiple completions same day

### 3. Rate Limiting
- Max 20 actions per hour (per action type)
- Prevents rapid spam
- User message: "You've accomplished a lot! Take a break and return with fresh energy."

### 4. Global Essence Cap
- Max 500 essence per hour
- Prevents coordinated farming across modules

### 5. Diminishing Returns
Essence rewards decrease after threshold:
```java
Tasks today < 5:  100% essence (20)
Tasks today < 10: 75% essence (15)
Tasks today < 15: 50% essence (10)
Tasks today >= 15: 25% essence (5)
```

### 6. Instant Completion Cooldown
- Minimum 1 minute between task creation and completion
- Prevents "create + instant complete" spam

## Database Schema

### Core Tables
- `game_profile` - User level, essence, title
- `essence_transaction` - Audit trail of all essence awarded
- `achievement` - Master list of achievements
- `user_achievement` - Achievements unlocked per user
- `reward` - Cosmetic items
- `user_reward` - Rewards owned per user

### Key Indexes
```sql
CREATE INDEX idx_essence_tx_user_time ON essence_transaction(user_id, timestamp);
CREATE INDEX idx_user_achievements ON user_achievement(user_id, unlocked_at);
```

## REST API Endpoints

### Profile
```http
GET /v1/gamification/profile
```
Returns user's game profile with level, essence, and progress.

### Transactions
```http
GET /v1/gamification/transactions
```
Returns recent essence transactions (last 50).

### Achievements
```http
GET /v1/gamification/achievements
```
Returns all achievements with unlock status.

### Rewards
```http
GET /v1/gamification/rewards
```
Returns all rewards with owned/equipped status.

### Equip Reward
```http
POST /v1/gamification/rewards/{rewardId}/equip
```
Equips a cosmetic reward (unequips others of same type).

## Configuration

Edit `application.yml`:
```yaml
gamification:
  essence:
    task-completed: 20
    habit-completed: 15
    streak-7-days: 50
    streak-30-days: 200
  limits:
    max-actions-per-hour: 20
    max-essence-per-hour: 500
    instant-completion-cooldown-minutes: 1
  diminishing-returns:
    enabled: true
```

## Integration Guide

### Step 1: Publish Events
When an action is completed, publish an event:
```java
@Service
public class YourService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void completeAction() {
        // ... your logic ...
        
        eventPublisher.publishEvent(new TaskCompletedEvent(
            this, taskId, userId, essenceAmount
        ));
    }
}
```

### Step 2: Mark Essence as Awarded
Add `essenceAwarded` field to your entity:
```java
@Column(name = "essence_awarded", nullable = false)
private Boolean essenceAwarded = false;
```

Set it before publishing the event:
```java
entity.setEssenceAwarded(true);
repository.save(entity);
```

## Future Enhancements

- [ ] Domain-specific levels (todo, habits, etc.)
- [ ] Seasonal achievements (limited-time)
- [ ] Cross-domain achievements requiring multiple modules
- [ ] Achievement rarity tiers (common, rare, legendary)
- [ ] Cosmetic shop (spend essence on new customizations)
- [ ] Guild/team achievements (shared progress)
- [ ] Streak tracking service
- [ ] Weekly/monthly leaderboards (opt-in)

## Testing

Run tests:
```bash
mvn test -Dtest=GamificationServiceTest
```

Key test scenarios:
- Essence calculation with diminishing returns
- Level progression accuracy
- Achievement trigger conditions
- Anti-cheat validation rules
- Event publishing and consumption
- Rate limit enforcement

## Philosophy

**Trust-Based Approach**
- Users are here for genuine growth, not to cheat themselves
- Technical safeguards prevent accidental exploitation
- No aggressive anti-cheat (no captchas, no invasive monitoring)
- Spiritual framing encourages honesty
- "This is your journey. The numbers only matter if they're real."

## Support

For questions or issues:
1. Check the integration guide above
2. Review event flow examples in tests
3. Verify configuration in `application.yml`
4. Check logs for rate limit or validation messages
