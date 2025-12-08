package gse.home.personalmanager.habit.application.service;

import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.gamification.config.GamificationConfig;
import gse.home.personalmanager.gamification.domain.event.HabitCompletedEvent;
import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.application.mapper.HabitLogMapper;
import gse.home.personalmanager.habit.domain.HabitFrequency;
import gse.home.personalmanager.habit.domain.HabitType;
import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import gse.home.personalmanager.habit.domain.service.HabitLogService;
import gse.home.personalmanager.habit.domain.service.HabitService;
import gse.home.personalmanager.habit.infrastructure.repository.HabitLogRepository;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitLogUseCaseServiceTest extends UnitTestBase {

    @Mock
    private HabitLogRepository habitLogRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitLogService habitLogService;

    @Mock
    private HabitService habitService;

    @Mock
    private HabitLogMapper habitLogMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private GamificationConfig gamificationConfig;

    @InjectMocks
    private HabitLogUseCaseService habitLogUseCaseService;

    private AppUser user;
    private Habit habit;
    private HabitLog habitLog;
    private HabitLogDTO habitLogDTO;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);

        habit = new Habit();
        habit.setId(1L);
        habit.setTitle("Test Habit");
        habit.setType(HabitType.YES_NO);
        habit.setFrequency(HabitFrequency.DAILY);
        habit.setUser(user);

        habitLog = new HabitLog();
        habitLog.setId(1L);
        habitLog.setHabit(habit);
        habitLog.setCreatedAt(LocalDate.now());
        habitLog.setCompleted(false);
        habitLog.setEssenceAwarded(false);

        habitLogDTO = new HabitLogDTO();
        habitLogDTO.setId(1L);
        habitLogDTO.setCompleted(true);
        habitLogDTO.setNumberOfTimes(1);
        habitLogDTO.setDuration(30);
        
        // Lenient stubs for config (may not be used in all tests)
        GamificationConfig.Essence essenceConfig = new GamificationConfig.Essence();
        essenceConfig.setHabitCompleted(10);
        lenient().when(gamificationConfig.getEssence()).thenReturn(essenceConfig);
    }

    @Test
    void getAllHabitLogs_shouldReturnListOfLogs() {
        // Given
        when(habitRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);
        when(habitLogRepository.findAllByHabitId(1L)).thenReturn(List.of(habitLog));
        when(habitLogMapper.toDto(habitLog)).thenReturn(habitLogDTO);

        // When
        var result = habitLogUseCaseService.getAllHabitLogs(1L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(habitRepository).existsByIdAndUserId(1L, 1L);
        verify(habitLogRepository).findAllByHabitId(1L);
        verify(habitLogMapper).toDto(habitLog);
    }

    @Test
    void getAllHabitLogs_shouldThrowException_whenHabitNotFound() {
        // Given
        when(habitRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> habitLogUseCaseService.getAllHabitLogs(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Habit not found or access denied");

        verify(habitRepository).existsByIdAndUserId(1L, 1L);
        verifyNoInteractions(habitLogRepository);
    }

    @Test
    void getAllHabitLogs_shouldReturnEmptyList_whenNoLogsExist() {
        // Given
        when(habitRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);
        when(habitLogRepository.findAllByHabitId(1L)).thenReturn(List.of());

        // When
        var result = habitLogUseCaseService.getAllHabitLogs(1L, 1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void createHabitLog_shouldCreateNewLog_whenLogDoesNotExist() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        doNothing().when(habitService).validateHabitLogForDate(habit, LocalDate.now());
        when(habitLogRepository.findByHabitIdAndCreatedAt(1L, LocalDate.now())).thenReturn(Optional.empty());
        when(habitLogRepository.save(any(HabitLog.class))).thenAnswer(invocation -> {
            HabitLog log = invocation.getArgument(0);
            log.setId(2L); // Simulate database assigning ID
            return log;
        });

        // When
        var result = habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO);

        // Then
        assertThat(result).isNotNull().isEqualTo(2L);
        verify(habitRepository).findByIdAndUserId(1L, 1L);
        verify(habitService).validateHabitLogForDate(habit, LocalDate.now());
        verify(habitLogRepository).findByHabitIdAndCreatedAt(1L, LocalDate.now());
        verify(habitLogService).updateLogProgress(any(), eq(habit), eq(1), eq(30), eq(true));
        verify(habitLogRepository).save(any());
    }

    @Test
    void createHabitLog_shouldUpdateExistingLog_whenLogExists() {
        // Given
        habitLog.setCompleted(false);
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        doNothing().when(habitService).validateHabitLogForDate(habit, LocalDate.now());
        when(habitLogRepository.findByHabitIdAndCreatedAt(1L, LocalDate.now())).thenReturn(Optional.of(habitLog));
        when(habitLogRepository.save(habitLog)).thenReturn(habitLog);

        // When
        var result = habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(habitLogService).updateLogProgress(habitLog, habit, 1, 30, true);
        verify(habitLogRepository).save(habitLog);
    }

    @Test
    void createHabitLog_shouldPublishEvent_whenCompletingForFirstTime() {
        // Given
        habitLog.setCompleted(false);
        habitLog.setEssenceAwarded(false);
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        doNothing().when(habitService).validateHabitLogForDate(habit, LocalDate.now());
        when(habitLogRepository.findByHabitIdAndCreatedAt(1L, LocalDate.now())).thenReturn(Optional.of(habitLog));
        when(habitLogRepository.save(habitLog)).thenReturn(habitLog);

        // When
        habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO);

        // Then
        ArgumentCaptor<HabitCompletedEvent> eventCaptor = ArgumentCaptor.forClass(HabitCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        HabitCompletedEvent event = eventCaptor.getValue();
        assertThat(event.getHabitId()).isEqualTo(1L);
        assertThat(event.getUserId()).isEqualTo(1L);
        assertThat(event.getEssenceAmount()).isEqualTo(10);
    }

    @Test
    void createHabitLog_shouldNotPublishEvent_whenAlreadyCompleted() {
        // Given
        habitLog.setCompleted(true);
        habitLog.setEssenceAwarded(true);
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        doNothing().when(habitService).validateHabitLogForDate(habit, LocalDate.now());
        when(habitLogRepository.findByHabitIdAndCreatedAt(1L, LocalDate.now())).thenReturn(Optional.of(habitLog));
        when(habitLogRepository.save(habitLog)).thenReturn(habitLog);

        // When
        habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO);

        // Then
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void createHabitLog_shouldMarkEssenceAwarded_whenCompletingForFirstTime() {
        // Given
        HabitLog spyLog = spy(habitLog);
        spyLog.setCompleted(false);
        spyLog.setEssenceAwarded(false);
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        doNothing().when(habitService).validateHabitLogForDate(habit, LocalDate.now());
        when(habitLogRepository.findByHabitIdAndCreatedAt(1L, LocalDate.now())).thenReturn(Optional.of(spyLog));
        when(habitLogRepository.save(spyLog)).thenReturn(spyLog);

        // When
        habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO);

        // Then
        verify(spyLog).setEssenceAwarded(true);
    }

    @Test
    void createHabitLog_shouldThrowException_whenHabitNotFound() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Habit not found or access denied");

        verify(habitRepository).findByIdAndUserId(1L, 1L);
        verifyNoInteractions(habitService);
        verifyNoInteractions(habitLogRepository);
    }

    @Test
    void createHabitLog_shouldThrowException_whenUserDoesNotOwnHabit() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitLogUseCaseService.createHabitLog(999L, 1L, habitLogDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Habit not found or access denied");
    }

    @Test
    void createHabitLog_shouldValidateLogDate() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        when(habitLogRepository.findByHabitIdAndCreatedAt(1L, LocalDate.now())).thenReturn(Optional.empty());
        when(habitLogRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        habitLogUseCaseService.createHabitLog(1L, 1L, habitLogDTO);

        // Then
        verify(habitService).validateHabitLogForDate(habit, LocalDate.now());
    }

    @Test
    void deleteHabitLog_shouldDeleteLog() {
        // Given
        when(habitRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);
        doNothing().when(habitLogRepository).deleteByIdAndHabitId(1L, 1L);

        // When
        habitLogUseCaseService.deleteHabitLog(1L, 1L, 1L);

        // Then
        verify(habitRepository).existsByIdAndUserId(1L, 1L);
        verify(habitLogRepository).deleteByIdAndHabitId(1L, 1L);
    }

    @Test
    void deleteHabitLog_shouldThrowException_whenHabitNotFound() {
        // Given
        when(habitRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> habitLogUseCaseService.deleteHabitLog(1L, 1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Habit not found or access denied");

        verify(habitRepository).existsByIdAndUserId(1L, 1L);
        verifyNoInteractions(habitLogRepository);
    }

    @Test
    void deleteHabitLog_shouldVerifyOwnership() {
        // Given
        when(habitRepository.existsByIdAndUserId(1L, 999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> habitLogUseCaseService.deleteHabitLog(999L, 1L, 1L))
                .isInstanceOf(RuntimeException.class);

        verify(habitRepository).existsByIdAndUserId(1L, 999L);
    }
}
