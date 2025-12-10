package gse.home.personalmanager.todo.infrastructure.repository;

import gse.home.personalmanager.todo.domain.model.TodoGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TodoGroupRepository extends JpaRepository<TodoGroup, Long> {

    List<TodoGroup> findAllByUserId(Long userId);

    Optional<TodoGroup> findAllByIdAndUserId(Long todoGroupId, Long userId);
}