package gse.home.personalmanager.todo.application.mapper;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.domain.model.Todo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    TodoDTO toDto(Todo todo);

    Todo toEntity(TodoDTO dto);

}
