package gse.home.personalmanager.gamification.application.dto;

import gse.home.personalmanager.gamification.domain.RewardType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RewardDTOTest {

    @Test
    void shouldCreateRewardDTO() {
        // Given
        RewardDTO dto = new RewardDTO();
        dto.setId("reward-1");
        dto.setName("Test Reward");
        dto.setDescription("A test reward");
        dto.setType(RewardType.TITLE);
        dto.setValue("Test Value");
        dto.setOwned(true);
        dto.setEquipped(false);

        // Then
        assertThat(dto.getId()).isEqualTo("reward-1");
        assertThat(dto.getName()).isEqualTo("Test Reward");
        assertThat(dto.getDescription()).isEqualTo("A test reward");
        assertThat(dto.getType()).isEqualTo(RewardType.TITLE);
        assertThat(dto.getValue()).isEqualTo("Test Value");
        assertThat(dto.getOwned()).isTrue();
        assertThat(dto.getEquipped()).isFalse();
    }
}
