package gse.home.personalmanager.habit.application.mapper;

import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.domain.model.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(source = "type", target = "habitType")
    @Mapping(source = "createAt", target = "createdAt")
    HabitDTO toDto(Habit habit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "logs", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(source = "habitType", target = "type")
    Habit toEntity(HabitDTO dto);

}
