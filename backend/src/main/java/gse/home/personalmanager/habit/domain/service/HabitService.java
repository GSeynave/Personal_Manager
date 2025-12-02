package gse.home.personalmanager.habit.domain.service;

import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.application.mapper.HabitMapper;
import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class HabitService {
    private final HabitMapper habitMapper;
    private final HabitRepository habitRepository;

    public Habit createHabitEntity(final HabitDTO dto, final AppUser user) {
        var entity = habitMapper.toEntity(dto);
        entity.setUser(user);
        return entity;
    }

    public Habit findEntityOrThrow(Long userId, Long id) {
        return habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.error("Habit with id: {} not found for user: {}, cannot proceed", id, userId);
                    return new RuntimeException("Habit not found");
                });
    }
}
