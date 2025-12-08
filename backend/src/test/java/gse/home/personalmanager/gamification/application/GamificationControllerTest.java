package gse.home.personalmanager.gamification.application;

import gse.home.personalmanager.gamification.application.dto.AchievementDTO;
import gse.home.personalmanager.gamification.application.dto.EssenceTransactionDTO;
import gse.home.personalmanager.gamification.application.dto.GameProfileDTO;
import gse.home.personalmanager.gamification.application.dto.RewardDTO;
import gse.home.personalmanager.gamification.application.service.GamificationUseCaseService;
import gse.home.personalmanager.gamification.domain.AchievementType;
import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.core.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GamificationController.class)
class GamificationControllerTest extends BaseControllerTest {

    @MockitoBean
    private GamificationUseCaseService useCaseService;

    @Test
    void getProfile_shouldReturnGameProfile() throws Exception {
        // Given
        GameProfileDTO profileDTO = new GameProfileDTO();
        profileDTO.setUserId(1L);
        profileDTO.setCurrentLevel(3);
        profileDTO.setCurrentTitle("Adventurer");
        profileDTO.setTotalEssence(250);
        profileDTO.setEssenceToNextLevel(150);
        profileDTO.setProgressToNextLevel(14.29);

        when(useCaseService.getProfile(1L)).thenReturn(profileDTO);

        // When & Then
        mockMvc.perform(get("/v1/gamification/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.currentLevel").value(3))
                .andExpect(jsonPath("$.currentTitle").value("Adventurer"))
                .andExpect(jsonPath("$.totalEssence").value(250))
                .andExpect(jsonPath("$.essenceToNextLevel").value(150))
                .andExpect(jsonPath("$.progressToNextLevel").value(14.29));

        verify(useCaseService).getProfile(1L);
    }

    @Test
    void getTransactions_shouldReturnEssenceTransactions() throws Exception {
        // Given
        EssenceTransactionDTO transaction1 = new EssenceTransactionDTO();
        transaction1.setId(1L);
        transaction1.setAmount(20);
        transaction1.setSource("task_completed");
        transaction1.setSourceId(10L);
        transaction1.setTimestamp(LocalDateTime.now());

        EssenceTransactionDTO transaction2 = new EssenceTransactionDTO();
        transaction2.setId(2L);
        transaction2.setAmount(15);
        transaction2.setSource("habit_completed");
        transaction2.setSourceId(5L);
        transaction2.setTimestamp(LocalDateTime.now().minusHours(1));

        when(useCaseService.getRecentTransactions(1L))
                .thenReturn(List.of(transaction1, transaction2));

        // When & Then
        mockMvc.perform(get("/v1/gamification/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(20))
                .andExpect(jsonPath("$[0].source").value("task_completed"))
                .andExpect(jsonPath("$[0].sourceId").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].amount").value(15))
                .andExpect(jsonPath("$[1].source").value("habit_completed"));

        verify(useCaseService).getRecentTransactions(1L);
    }

    @Test
    void getAchievements_shouldReturnAllAchievements() throws Exception {
        // Given
        AchievementDTO achievement1 = new AchievementDTO();
        achievement1.setId("task_10");
        achievement1.setName("Task Master");
        achievement1.setDescription("Complete 10 tasks");
        achievement1.setType(AchievementType.MILESTONE);
        achievement1.setEssenceReward(50);
        achievement1.setUnlocked(true);

        AchievementDTO achievement2 = new AchievementDTO();
        achievement2.setId("streak_7");
        achievement2.setName("Weekly Warrior");
        achievement2.setDescription("Maintain a 7-day streak");
        achievement2.setType(AchievementType.STREAK);
        achievement2.setEssenceReward(75);
        achievement2.setUnlocked(false);

        when(useCaseService.getAllAchievements(1L))
                .thenReturn(List.of(achievement1, achievement2));

        // When & Then
        mockMvc.perform(get("/v1/gamification/achievements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("task_10"))
                .andExpect(jsonPath("$[0].name").value("Task Master"))
                .andExpect(jsonPath("$[0].type").value("MILESTONE"))
                .andExpect(jsonPath("$[0].essenceReward").value(50))
                .andExpect(jsonPath("$[0].unlocked").value(true))
                .andExpect(jsonPath("$[1].id").value("streak_7"))
                .andExpect(jsonPath("$[1].unlocked").value(false));

        verify(useCaseService).getAllAchievements(1L);
    }

    @Test
    void getRewards_shouldReturnAllRewards() throws Exception {
        // Given
        RewardDTO reward1 = new RewardDTO();
        reward1.setId("emoji_fire");
        reward1.setName("Fire Emoji");
        reward1.setDescription("Blazing hot!");
        reward1.setType(RewardType.EMOJI);
        reward1.setValue("🔥");
        reward1.setOwned(true);
        reward1.setEquipped(true);

        RewardDTO reward2 = new RewardDTO();
        reward2.setId("badge_bronze");
        reward2.setName("Bronze Badge");
        reward2.setDescription("Your first badge");
        reward2.setType(RewardType.BORDER);
        reward2.setValue("bronze");
        reward2.setOwned(true);
        reward2.setEquipped(false);

        when(useCaseService.getAllRewards(1L))
                .thenReturn(List.of(reward1, reward2));

        // When & Then
        mockMvc.perform(get("/v1/gamification/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("emoji_fire"))
                .andExpect(jsonPath("$[0].name").value("Fire Emoji"))
                .andExpect(jsonPath("$[0].type").value("EMOJI"))
                .andExpect(jsonPath("$[0].value").value("🔥"))
                .andExpect(jsonPath("$[0].owned").value(true))
                .andExpect(jsonPath("$[0].equipped").value(true))
                .andExpect(jsonPath("$[1].id").value("badge_bronze"))
                .andExpect(jsonPath("$[1].owned").value(true))
                .andExpect(jsonPath("$[1].equipped").value(false));

        verify(useCaseService).getAllRewards(1L);
    }

    @Test
    void equipReward_shouldEquipRewardSuccessfully() throws Exception {
        // Given
        doNothing().when(useCaseService).equipReward(1L, "emoji_fire");

        // When & Then
        mockMvc.perform(post("/v1/gamification/rewards/emoji_fire/equip")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(useCaseService).equipReward(1L, "emoji_fire");
    }

}

