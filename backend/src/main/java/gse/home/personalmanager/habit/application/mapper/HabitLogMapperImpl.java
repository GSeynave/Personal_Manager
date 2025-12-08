package gse.home.personalmanager.habit.application.mapper;

import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import org.springframework.stereotype.Component;

@Component
public class HabitLogMapperImpl implements HabitLogMapper {

    @Override
    public HabitLogDTO toDto(HabitLog habitLog) {
        if (habitLog == null) {
            return null;
        }
        
        HabitLogDTO dto = new HabitLogDTO();
        dto.setId(habitLog.getId());
        dto.setHabitId(habitLog.getHabit() != null ? habitLog.getHabit().getId() : null);
        dto.setCreatedAt(habitLog.getCreatedAt());
        dto.setCompleted(habitLog.getCompleted());
        dto.setNumberOfTimes(habitLog.getCount());
        dto.setDuration(habitLog.getDuration());
        
        return dto;
    }

    @Override
    public HabitLog toEntity(HabitLogDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HabitLog habitLog = new HabitLog();
        // Note: habit, id, and createdAt are ignored as per mapper definition
        habitLog.setCompleted(dto.getCompleted());
        habitLog.setCount(dto.getNumberOfTimes());
        habitLog.setDuration(dto.getDuration());
        
        return habitLog;
    }
}
