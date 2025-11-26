package gse.home.personalmanager.todo.infrastructure.repository;

import gse.home.personalmanager.todo.domain.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
}