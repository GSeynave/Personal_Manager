package gse.home.personalmanager.todo.infrastructure.repository;

import gse.home.personalmanager.todo.domain.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserId(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}