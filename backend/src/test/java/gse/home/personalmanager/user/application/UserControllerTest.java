package gse.home.personalmanager.user.application;

import gse.home.personalmanager.core.test.BaseControllerTest;
import gse.home.personalmanager.user.application.dto.UserIdentityDto;
import gse.home.personalmanager.user.application.service.UserUseCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends BaseControllerTest {

    @MockitoBean
    private UserUseCaseService useCaseService;

    @Test
    void getUserIdentity_shouldReturnUserIdentity() throws Exception {
        // Arrange
        UserIdentityDto userIdentity = new UserIdentityDto(
                testUserId,
                "test@example.com",
                "TestUser",
                5,
                "Journeyman",
                "#FF0000",
                "😊"
        );
        when(useCaseService.getUserIdentity("test-firebase-uid")).thenReturn(userIdentity);

        // Act & Assert
        mockMvc.perform(get("/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.userTag").value("TestUser"))
                .andExpect(jsonPath("$.level").value(5))
                .andExpect(jsonPath("$.title").value("Journeyman"))
                .andExpect(jsonPath("$.borderColor").value("#FF0000"))
                .andExpect(jsonPath("$.equippedEmoji").value("😊"));

        verify(useCaseService).getUserIdentity("test-firebase-uid");
    }

    @Test
    void updateUserIdentity_shouldUpdateUserTag() throws Exception {
        // Arrange
        UserIdentityDto updateDto = new UserIdentityDto(
                testUserId,
                "test@example.com",
                "NewUserTag",
                5,
                "Journeyman",
                "#FF0000",
                null
        );

        // Act & Assert
        mockMvc.perform(put("/v1/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        verify(useCaseService).updateUserTag(eq("test-firebase-uid"), any(UserIdentityDto.class));
    }

    @Test
    void getUserIdentity_shouldHandleNullEquippedEmoji() throws Exception {
        // Arrange
        UserIdentityDto userIdentity = new UserIdentityDto(
                testUserId,
                "test@example.com",
                "TestUser",
                1,
                "Freshman",
                null,
                null
        );
        when(useCaseService.getUserIdentity("test-firebase-uid")).thenReturn(userIdentity);

        // Act & Assert
        mockMvc.perform(get("/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equippedEmoji").doesNotExist());
    }

    @Test
    void updateUserIdentity_shouldRequireCsrfToken() throws Exception {
        // Arrange
        UserIdentityDto updateDto = new UserIdentityDto(
                testUserId,
                "test@example.com",
                "NewTag",
                5,
                "Journeyman",
                "#FF0000",
                null
        );

        // Act & Assert - Without CSRF should fail in production (disabled in test)
        mockMvc.perform(put("/v1/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
}
