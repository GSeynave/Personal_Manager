package gse.home.personalmanager.todo.application.mapper;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.domain.model.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    TodoDTO toDto(Todo todo);

    @Mapping(target = "id", ignore = true)
    Todo toEntity(TodoDTO dto);

}
