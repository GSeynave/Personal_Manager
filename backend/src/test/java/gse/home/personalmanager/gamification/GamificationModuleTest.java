package gse.home.personalmanager.gamification;

import gse.home.personalmanager.gamification.domain.service.GamificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test to verify gamification level progression formula
 */
@ExtendWith(MockitoExtension.class)
public class GamificationModuleTest {

    @InjectMocks
    private GamificationService gamificationService;

    @Test
    public void testGamificationServiceCreation() {
        assertNotNull(gamificationService, "GamificationService should be created");
    }

    @Test
    public void testLevelProgressionFormula() {
        // Test the exponential curve: essence = 100 * level^2
        assertEquals(0, gamificationService.calculateRequiredEssenceForLevel(1));
        assertEquals(400, gamificationService.calculateRequiredEssenceForLevel(2));
        assertEquals(900, gamificationService.calculateRequiredEssenceForLevel(3));
        assertEquals(1600, gamificationService.calculateRequiredEssenceForLevel(4));
        assertEquals(2500, gamificationService.calculateRequiredEssenceForLevel(5));
    }
}
