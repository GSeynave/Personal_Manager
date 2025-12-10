package gse.home.personalmanager.gamification;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Gamification Module
 * 
 * Cross-cutting gamification system that rewards user actions across all domains
 * with meaningful progression and cosmetic rewards.
 * 
 * Features:
 * - Essence-based progression currency
 * - Level system with titles
 * - Achievement system (milestones, streaks, domain mastery)
 * - Cosmetic rewards (titles, borders, emojis, name customization)
 * - Event-driven architecture for decoupled integration
 * - Anti-cheat mechanisms (rate limiting, diminishing returns, cooldowns)
 */
@Configuration
@ComponentScan(basePackages = "gse.home.personalmanager.gamification")
public class GamificationModule {
}
