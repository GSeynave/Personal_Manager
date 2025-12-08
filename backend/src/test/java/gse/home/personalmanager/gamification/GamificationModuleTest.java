package gse.home.personalmanager.gamification;

import gse.home.personalmanager.gamification.domain.service.GamificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic integration test to verify gamification module is properly wired
 */
@SpringBootTest
public class GamificationModuleTest {

    @Autowired(required = false)
    private GamificationService gamificationService;

    @Test
    public void testGamificationModuleLoaded() {
        assertNotNull(gamificationService, "GamificationService should be loaded");
    }

    @Test
    public void testLevelProgressionFormula() {
        // Test the exponential curve
        assertEquals(0, gamificationService.calculateRequiredEssenceForLevel(1));
        assertEquals(400, gamificationService.calculateRequiredEssenceForLevel(2));
        assertEquals(900, gamificationService.calculateRequiredEssenceForLevel(3));
        assertEquals(1600, gamificationService.calculateRequiredEssenceForLevel(4));
        assertEquals(2500, gamificationService.calculateRequiredEssenceForLevel(5));
    }
}
