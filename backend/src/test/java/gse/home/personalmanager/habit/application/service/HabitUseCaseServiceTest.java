package gse.home.personalmanager.habit.application.service;

import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.application.mapper.HabitMapper;
import gse.home.personalmanager.habit.domain.HabitCategory;
import gse.home.personalmanager.habit.domain.HabitFrequency;
import gse.home.personalmanager.habit.domain.HabitType;
import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.habit.domain.service.HabitService;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitUseCaseServiceTest extends UnitTestBase {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitMapper habitMapper;

    @Mock
    private HabitService habitService;

    @InjectMocks
    private HabitUseCaseService habitUseCaseService;

    private AppUser user;
    private Habit habit;
    private HabitDTO habitDTO;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@example.com");

        habit = new Habit();
        habit.setId(1L);
        habit.setTitle("Morning Exercise");
        habit.setDescription("30 minutes of exercise");
        habit.setType(HabitType.YES_NO);
        habit.setFrequency(HabitFrequency.DAILY);
        habit.setCategory(HabitCategory.HEALTH);
        habit.setUser(user);

        habitDTO = new HabitDTO();
        habitDTO.setId(1L);
        habitDTO.setTitle("Morning Exercise");
        habitDTO.setDescription("30 minutes of exercise");
        habitDTO.setHabitType(HabitType.YES_NO);
        habitDTO.setFrequency(HabitFrequency.DAILY);
        habitDTO.setCategory(HabitCategory.HEALTH);
    }

    @Test
    void getAllHabits_shouldReturnListOfHabits() {
        // Given
        when(habitRepository.findAllByUserId(1L)).thenReturn(List.of(habit));
        when(habitMapper.toDto(habit)).thenReturn(habitDTO);

        // When
        var result = habitUseCaseService.getAllHabits(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Morning Exercise");
        verify(habitRepository).findAllByUserId(1L);
        verify(habitMapper).toDto(habit);
    }

    @Test
    void getAllHabits_shouldReturnEmptyList_whenNoHabitsExist() {
        // Given
        when(habitRepository.findAllByUserId(1L)).thenReturn(List.of());

        // When
        var result = habitUseCaseService.getAllHabits(1L);

        // Then
        assertThat(result).isEmpty();
        verify(habitRepository).findAllByUserId(1L);
        verifyNoInteractions(habitMapper);
    }

    @Test
    void createHabit_shouldCreateAndReturnHabitId() {
        // Given
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(habitMapper.toEntity(habitDTO)).thenReturn(habit);
        doNothing().when(habitService).validateHabitConsistency(habit);
        when(habitRepository.save(habit)).thenReturn(habit);

        // When
        var result = habitUseCaseService.createHabit(1L, habitDTO);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(userRepository).getReferenceById(1L);
        verify(habitMapper).toEntity(habitDTO);
        verify(habitService).validateHabitConsistency(habit);
        verify(habitRepository).save(habit);
    }

    @Test
    void createHabit_shouldSetUserReference() {
        // Given
        ArgumentCaptor<Habit> habitCaptor = ArgumentCaptor.forClass(Habit.class);
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(habitMapper.toEntity(habitDTO)).thenReturn(habit);
        doNothing().when(habitService).validateHabitConsistency(any());
        when(habitRepository.save(any())).thenReturn(habit);

        // When
        habitUseCaseService.createHabit(1L, habitDTO);

        // Then
        verify(habitRepository).save(habitCaptor.capture());
        assertThat(habitCaptor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void createHabit_shouldCallValidation() {
        // Given
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(habitMapper.toEntity(habitDTO)).thenReturn(habit);
        when(habitRepository.save(habit)).thenReturn(habit);

        // When
        habitUseCaseService.createHabit(1L, habitDTO);

        // Then
        verify(habitService).validateHabitConsistency(habit);
    }

    @Test
    void updateHabit_shouldUpdateExistingHabit() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(habit);

        // When
        habitUseCaseService.updateHabit(1L, 1L, habitDTO);

        // Then
        verify(habitRepository).findByIdAndUserId(1L, 1L);
        verify(habitMapper).updateEntityFromDto(habitDTO, habit);
        verify(habitRepository).save(habit);
    }

    @Test
    void updateHabit_shouldThrowException_whenHabitNotFound() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitUseCaseService.updateHabit(1L, 1L, habitDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Habit not found");

        verify(habitRepository).findByIdAndUserId(1L, 1L);
        verifyNoInteractions(habitService);
        verify(habitRepository, never()).save(any());
    }

    @Test
    void updateHabit_shouldThrowException_whenUserDoesNotOwnHabit() {
        // Given
        when(habitRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitUseCaseService.updateHabit(999L, 1L, habitDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Habit not found");
    }

    @Test
    void deleteHabit_shouldDeleteHabit() {
        // Given
        doNothing().when(habitRepository).deleteByIdAndUserId(1L, 1L);

        // When
        habitUseCaseService.deleteHabit(1L, 1L);

        // Then
        verify(habitRepository).deleteByIdAndUserId(1L, 1L);
    }

    @Test
    void deleteHabit_shouldOnlyDeleteForCorrectUser() {
        // Given
        doNothing().when(habitRepository).deleteByIdAndUserId(1L, 1L);

        // When
        habitUseCaseService.deleteHabit(1L, 1L);

        // Then
        verify(habitRepository).deleteByIdAndUserId(1L, 1L);
        verify(habitRepository, never()).deleteByIdAndUserId(eq(1L), eq(999L));
    }
}
