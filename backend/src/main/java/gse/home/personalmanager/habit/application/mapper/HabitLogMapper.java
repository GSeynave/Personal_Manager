package gse.home.personalmanager.habit.application.mapper;

import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitLogMapper {

    @Mapping(source = "habit.id", target = "habitId")
    @Mapping(source = "count", target = "numberOfTimes")
    HabitLogDTO toDto(HabitLog habitLog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "habit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "numberOfTimes", target = "count")
    HabitLog toEntity(HabitLogDTO dto);

}
