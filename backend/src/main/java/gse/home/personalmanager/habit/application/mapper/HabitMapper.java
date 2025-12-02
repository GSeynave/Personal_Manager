package gse.home.personalmanager.habit.application.mapper;

import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.domain.model.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(target = "createdAt", source = "created_at")
    @Mapping(target = "lastModified", source = "last_modified")
    HabitDTO toDto(Habit habit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", source = "createdAt")
    @Mapping(target = "last_modified", source = "lastModified")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "logs", ignore = true)
    @Mapping(target = "version", ignore = true)
    Habit toEntity(HabitDTO dto);

}
