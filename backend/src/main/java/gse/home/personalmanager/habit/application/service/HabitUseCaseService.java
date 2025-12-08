package gse.home.personalmanager.habit.application.service;

import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.application.mapper.HabitMapper;
import gse.home.personalmanager.habit.domain.service.HabitService;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HabitUseCaseService {
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final HabitService habitService;

    /**
     * Retrieves all habits for a specified user.
     *
     * @param userId the ID of the user whose habits are to be retrieved
     * @return a list of {@code HabitDTO} containing all habits for the user
     */
    @Transactional
    public List<HabitDTO> getAllHabits(final Long userId) {
        log.info("UseCaseService: Getting all habits for user id: {}.", userId);
        var habits = habitRepository.findAllByUserId(userId);
        return habits.stream()
                .map(habitMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new habit for a specified user.
     *
     * @param userId   the ID of the user for whom the habit is being created
     * @param habitDTO the {@code HabitDTO} object containing the details of the habit to be created
     * @return the ID of the newly created habit
     */
    @Transactional
    public Long createHabit(final Long userId, final HabitDTO habitDTO) {
        log.info("UseCaseService: Creating a new habit for user id: {}.", userId);


        // Step 1: Prepare
        var userRef = userRepository.getReferenceById(userId);

        // Step 2: Create an entity
        var habitEntity = habitMapper.toEntity(habitDTO);
        habitEntity.setUser(userRef);

        // Step 3: Validation
        habitService.validateHabitConsistency(habitEntity);

        // Step 4: persist
        return habitRepository.save(habitEntity).getId();
    }

    /**
     * Updates an existing habit for a specific user.
     *
     * @param userId   the ID of the user who owns the habit
     * @param id       the ID of the habit to be updated
     * @param habitDTO the data transfer object containing the updated details of the habit
     */
    @Transactional
    public void updateHabit(final Long userId, final Long id, final HabitDTO habitDTO) {
        log.info("UseCaseService: Updating the habit id: {}, and UserId : {}", id, userId);

        // Step 1: find entity or throw exception
        var entity = habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.error("Habit with id: {} not found for user: {}, cannot proceed", id, userId);
                    return new RuntimeException("Habit not found");
                });

        // Step 2: update entity
        entity.updateDetails(
                habitDTO.getTitle(),
                habitDTO.getDescription(),
                habitDTO.getCategory(),
                habitDTO.getFrequency(),
                habitDTO.getScheduledDays(),
                habitDTO.getNumberOfTimes(),
                habitDTO.getDuration()
        );

        // Step 3: persist
        habitRepository.save(entity);
    }

    /**
     * Deletes a habit for a specific user.
     *
     * @param userId the ID of the user who owns the habit
     * @param id     the ID of the habit to be deleted
     */
    @Transactional
    public void deleteHabit(final Long userId, final Long id) {
        log.info("UseCaseService: Deleting the habit id: {} for user: {}", id, userId);
        habitRepository.deleteByIdAndUserId(id, userId);
    }
}
