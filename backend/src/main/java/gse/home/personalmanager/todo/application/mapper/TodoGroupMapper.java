package gse.home.personalmanager.todo.application.mapper;

import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TodoGroupMapper {

    TodoGroupDTO toDto(TodoGroup todoGroup);

    @Mapping(target = "id", ignore = true)
    TodoGroup toEntity(TodoGroupDTO groupDTO);

}
