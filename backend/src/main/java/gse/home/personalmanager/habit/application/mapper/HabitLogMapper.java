package gse.home.personalmanager.habit.application.mapper;

import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitLogMapper {

    @Mapping(target = "habitId", source = "habit.id")
    @Mapping(target = "createdAt", source = "created_at")
    HabitLogDTO toDto(HabitLog habitLog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", source = "createdAt")
    @Mapping(target = "habit", ignore = true)
    HabitLog toEntity(HabitLogDTO dto);

}
