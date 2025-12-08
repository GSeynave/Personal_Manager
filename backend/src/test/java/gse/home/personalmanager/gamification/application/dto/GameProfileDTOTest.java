package gse.home.personalmanager.gamification.application.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GameProfileDTOTest {

    @Test
    void shouldCreateGameProfileDTO() {
        // Given
        GameProfileDTO dto = new GameProfileDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setUserId(123L);
        dto.setTotalEssence(1000);
        dto.setCurrentLevel(5);
        dto.setCurrentTitle("Master");
        dto.setLastEssenceEarned(now);
        dto.setEssenceToNextLevel(500);
        dto.setProgressToNextLevel(0.75);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(123L);
        assertThat(dto.getTotalEssence()).isEqualTo(1000);
        assertThat(dto.getCurrentLevel()).isEqualTo(5);
        assertThat(dto.getCurrentTitle()).isEqualTo("Master");
        assertThat(dto.getLastEssenceEarned()).isEqualTo(now);
        assertThat(dto.getEssenceToNextLevel()).isEqualTo(500);
        assertThat(dto.getProgressToNextLevel()).isEqualTo(0.75);
    }
}
