# Gamification Module - Quick Start Guide

## 🚀 Getting Started

### Prerequisites
- Java 17+
- PostgreSQL database
- Spring Boot application running
- Todo and Habit modules active

### Installation

1. **Database Setup**
   
   The tables will be auto-created by Hibernate, but you can also run the migration script manually:
   ```bash
   psql -U your_user -d your_database -f backend/src/main/resources/db/migration/V1__gamification_schema.sql
   ```

2. **Configuration**
   
   Verify `application.yml` contains:
   ```yaml
   gamification:
     essence:
       task-completed: 20
       habit-completed: 15
     limits:
       max-actions-per-hour: 20
       max-essence-per-hour: 500
   ```

3. **Start Application**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

   The gamification module will:
   - ✅ Auto-initialize default achievements
   - ✅ Auto-initialize default rewards
   - ✅ Start listening for completion events

### Testing the Module

#### 1. Complete a Task
```bash
# Complete a todo via API
curl -X PUT http://localhost:8080/v1/todos/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"completed": true}'
```

**Expected Result:**
- Task marked complete
- TaskCompletedEvent published
- 20 essence awarded to user
- "First Steps" achievement unlocked (if first task)
- Profile updated with new essence

#### 2. Check Your Profile
```bash
curl http://localhost:8080/v1/gamification/profile \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "totalEssence": 20,
  "currentLevel": 1,
  "currentTitle": "Freshman",
  "essenceToNextLevel": 380,
  "progressToNextLevel": 5.0
}
```

#### 3. View Achievements
```bash
curl http://localhost:8080/v1/gamification/achievements \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
[
  {
    "id": "first_task",
    "name": "First Steps",
    "description": "Complete your first task",
    "type": "MILESTONE",
    "essenceReward": 50,
    "unlocked": true
  },
  ...
]
```

#### 4. View Rewards
```bash
curl http://localhost:8080/v1/gamification/rewards \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
[
  {
    "id": "emoji_fire",
    "name": "Fire Emoji",
    "type": "EMOJI",
    "value": "🔥",
    "owned": true,
    "equipped": false
  },
  ...
]
```

#### 5. Equip a Reward
```bash
curl -X POST http://localhost:8080/v1/gamification/rewards/emoji_fire/equip \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Monitoring

#### Check Logs
```bash
tail -f logs/application.log | grep -i gamification
```

**Expected Log Output:**
```
2025-12-08 10:30:15 INFO  GamificationEventListener - Handling task completion for user 123, task 1
2025-12-08 10:30:15 INFO  GamificationService - Awarded 20 essence to user 123 for task_completed (sourceId: 1)
2025-12-08 10:30:15 INFO  AchievementService - Achievement unlocked: First Steps for user 123
```

#### Check Database
```sql
-- View user profile
SELECT * FROM game_profile WHERE user_id = 123;

-- View essence transactions
SELECT * FROM essence_transaction WHERE user_id = 123 ORDER BY timestamp DESC;

-- View unlocked achievements
SELECT * FROM user_achievement WHERE user_id = 123;
```

### Troubleshooting

#### Issue: No essence awarded
**Possible Causes:**
1. Event not published (check TodoUseCaseService/HabitLogUseCaseService)
2. User not found
3. Rate limit reached (20 actions/hour)
4. Essence cap reached (500/hour)

**Solution:**
```bash
# Check logs for rate limit messages
grep "hit hourly" logs/application.log

# Check essence transactions in last hour
SELECT SUM(amount) FROM essence_transaction 
WHERE user_id = 123 
AND timestamp > NOW() - INTERVAL '1 hour';
```

#### Issue: Achievement not unlocking
**Possible Causes:**
1. Achievement criteria not met
2. Achievement already unlocked
3. Achievement not active

**Solution:**
```sql
-- Check achievement status
SELECT * FROM achievement WHERE id = 'first_task';

-- Check if already unlocked
SELECT * FROM user_achievement WHERE user_id = 123 AND achievement_id = 'first_task';

-- Check transaction count
SELECT COUNT(*) FROM essence_transaction 
WHERE user_id = 123 AND source = 'task_completed';
```

#### Issue: Level not increasing
**Check Required Essence:**
- Level 2 requires: 400 essence (not just 20!)
- Level 3 requires: 900 essence
- Formula: 100 * level²

```bash
curl http://localhost:8080/v1/gamification/profile \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Look at `essenceToNextLevel` field.

### API Endpoints Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/v1/gamification/profile` | Get user game profile |
| GET | `/v1/gamification/transactions` | Get essence transaction history |
| GET | `/v1/gamification/achievements` | Get all achievements with unlock status |
| GET | `/v1/gamification/rewards` | Get all rewards with owned/equipped status |
| POST | `/v1/gamification/rewards/{id}/equip` | Equip a cosmetic reward |

### Next Steps

1. **Frontend Integration**
   - Display profile card with level/essence
   - Show achievement notifications
   - Create reward customization UI

2. **Test Progression**
   - Complete 10+ tasks to test diminishing returns
   - Verify rate limiting after 20 tasks in an hour
   - Test level up at 400 essence

3. **Customize**
   - Adjust essence values in `application.yml`
   - Add new achievements via `GamificationDataInitializer`
   - Create domain-specific achievements

### Support

- Check `backend/src/main/java/gse/home/personalmanager/gamification/README.md`
- Review implementation at root `/GAMIFICATION_IMPLEMENTATION.md`
- Check logs: `logs/application.log`

## 🎉 You're Ready!

The gamification module is now active and will automatically reward users as they complete tasks and habits!
