-- Gamification Module Database Schema
-- Version: 1.0.0
-- Description: Core gamification system with essence, levels, achievements, and rewards

-- 1. Game Profile Table
CREATE TABLE IF NOT EXISTS game_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_essence INTEGER NOT NULL DEFAULT 0,
    current_level INTEGER NOT NULL DEFAULT 1,
    current_title VARCHAR(50) NOT NULL DEFAULT 'Freshman',
    last_essence_earned TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_game_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_game_profile_user ON game_profile(user_id);

-- 2. Essence Transaction Table (Audit Trail)
CREATE TABLE IF NOT EXISTS essence_transaction (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount INTEGER NOT NULL,
    source VARCHAR(50) NOT NULL,
    source_id BIGINT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_essence_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_essence_tx_user_time ON essence_transaction(user_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_essence_tx_source ON essence_transaction(source, source_id);

-- 3. Achievement Table
CREATE TABLE IF NOT EXISTS achievement (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    essence_reward INTEGER DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 4. Achievement Rewards (ElementCollection)
CREATE TABLE IF NOT EXISTS achievement_rewards (
    achievement_id VARCHAR(50) NOT NULL,
    reward_id VARCHAR(50) NOT NULL,
    CONSTRAINT fk_achievement_rewards FOREIGN KEY (achievement_id) REFERENCES achievement(id) ON DELETE CASCADE
);

-- 5. User Achievement Table
CREATE TABLE IF NOT EXISTS user_achievement (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id VARCHAR(50) NOT NULL,
    unlocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_achievement_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_achievement UNIQUE (user_id, achievement_id)
);

CREATE INDEX IF NOT EXISTS idx_user_achievements ON user_achievement(user_id, unlocked_at);

-- 6. Reward Table
CREATE TABLE IF NOT EXISTS reward (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    value VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 7. User Reward Table
CREATE TABLE IF NOT EXISTS user_reward (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reward_id VARCHAR(50) NOT NULL,
    is_equipped BOOLEAN NOT NULL DEFAULT FALSE,
    unlocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_reward_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_reward UNIQUE (user_id, reward_id)
);

CREATE INDEX IF NOT EXISTS idx_user_rewards ON user_reward(user_id, unlocked_at);

-- 8. Add essenceAwarded field to existing tables
ALTER TABLE todo ADD COLUMN IF NOT EXISTS essence_awarded BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE habit_log ADD COLUMN IF NOT EXISTS essence_awarded BOOLEAN NOT NULL DEFAULT FALSE;

-- 9. Insert default achievements
INSERT INTO achievement (id, name, description, type, essence_reward, active) VALUES
    ('first_task', 'First Steps', 'Complete your first task', 'MILESTONE', 50, TRUE),
    ('task_10', 'Getting Started', 'Complete 10 tasks', 'CUMULATIVE', 100, TRUE),
    ('task_50', 'Productive', 'Complete 50 tasks', 'CUMULATIVE', 250, TRUE),
    ('task_100', 'Task Master', 'Complete 100 tasks', 'CUMULATIVE', 500, TRUE),
    ('first_habit', 'New Habits', 'Complete your first habit', 'MILESTONE', 50, TRUE),
    ('habit_10', 'Building Routine', 'Complete 10 habits', 'CUMULATIVE', 100, TRUE),
    ('habit_50', 'Consistency', 'Complete 50 habits', 'CUMULATIVE', 250, TRUE)
ON CONFLICT (id) DO NOTHING;

-- 10. Insert default rewards
INSERT INTO reward (id, name, description, type, value, active) VALUES
    ('title_apprentice', 'Apprentice', 'Display "the Apprentice" title', 'TITLE', 'the Apprentice', TRUE),
    ('title_journeyman', 'Journeyman', 'Display "the Journeyman" title', 'TITLE', 'the Journeyman', TRUE),
    ('title_master', 'Master', 'Display "the Master" title', 'TITLE', 'the Master', TRUE),
    ('border_bronze', 'Bronze Border', 'Bronze profile border', 'BORDER', '#CD7F32', TRUE),
    ('border_silver', 'Silver Border', 'Silver profile border', 'BORDER', '#C0C0C0', TRUE),
    ('border_gold', 'Gold Border', 'Gold profile border', 'BORDER', '#FFD700', TRUE),
    ('emoji_fire', 'Fire Emoji', 'Fire emoji set 🔥', 'EMOJI', '🔥', TRUE),
    ('emoji_star', 'Star Emoji', 'Star emoji set ⭐', 'EMOJI', '⭐', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Link rewards to achievements
INSERT INTO achievement_rewards (achievement_id, reward_id) VALUES
    ('first_task', 'emoji_fire'),
    ('task_10', 'border_bronze'),
    ('task_50', 'title_apprentice'),
    ('task_100', 'title_journeyman'),
    ('first_habit', 'emoji_star'),
    ('habit_50', 'border_silver')
ON CONFLICT DO NOTHING;
