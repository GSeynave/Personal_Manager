package gse.home.personalmanager.gamification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "gamification")
public class GamificationConfig {

    private Essence essence = new Essence();
    private Limits limits = new Limits();
    private DiminishingReturns diminishingReturns = new DiminishingReturns();

    @Data
    public static class Essence {
        private Integer taskCompleted = 20;
        private Integer habitCompleted = 15;
        private Integer streak7Days = 50;
        private Integer streak30Days = 200;
        private Integer streak100Days = 500;
    }

    @Data
    public static class Limits {
        private Integer maxActionsPerHour = 20;
        private Integer maxEssencePerHour = 500;
        private Integer instantCompletionCooldownMinutes = 1;
    }

    @Data
    public static class DiminishingReturns {
        private Boolean enabled = true;
        private List<Threshold> thresholds = new ArrayList<>();

        @Data
        public static class Threshold {
            private Integer tasks;
            private Double multiplier;
        }
    }

    /**
     * Calculate task essence with diminishing returns
     */
    public int calculateTaskEssence(int tasksCompletedToday) {
        if (!diminishingReturns.getEnabled()) {
            return essence.getTaskCompleted();
        }

        int baseEssence = essence.getTaskCompleted();

        // Apply diminishing returns based on tasks completed today
        if (tasksCompletedToday < 5) {
            return baseEssence;
        } else if (tasksCompletedToday < 10) {
            return (int) (baseEssence * 0.75);
        } else if (tasksCompletedToday < 15) {
            return (int) (baseEssence * 0.5);
        } else {
            return (int) (baseEssence * 0.25);
        }
    }

    /**
     * Calculate habit essence with diminishing returns
     */
    public int calculateHabitEssence(int habitsCompletedToday) {
        if (!diminishingReturns.getEnabled()) {
            return essence.getHabitCompleted();
        }

        int baseEssence = essence.getHabitCompleted();

        // Apply diminishing returns based on habits completed today
        if (habitsCompletedToday < 5) {
            return baseEssence;
        } else if (habitsCompletedToday < 10) {
            return (int) (baseEssence * 0.75);
        } else {
            return (int) (baseEssence * 0.5);
        }
    }
}
