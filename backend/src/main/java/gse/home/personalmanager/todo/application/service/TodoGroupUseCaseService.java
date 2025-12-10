package gse.home.personalmanager.todo.application.service;

import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.mapper.TodoGroupMapper;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TodoGroupUseCaseService {
    private final EntityManager entityManager;
    private final TodoGroupRepository repository;
    private final TodoGroupMapper mapper;


    @Transactional
    public Long createTodoGroup(final TodoGroupDTO todoGroupDTO) {
        log.info("UseCaseService: Creating a new todo group");
        var entity = mapper.toEntity(todoGroupDTO);
        entity.setUser(getUserReference());

        return repository.save(entity).getId();
    }

    public void deleteTodoGroup(Long todoGroupId) {
        var group = repository.findAllByIdAndUserId(todoGroupId, getUserId()).orElseThrow();
        if (!group.getTodos().isEmpty()) {
            log.warn("Cannot delete a group with todos. Group id: {}", todoGroupId);
            return;
        }
        repository.deleteById(todoGroupId);
    }

    public Long getUserId() {
        var principal = (AppUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser().getId();
    }

    public AppUser getUserReference() {
        var userId = getUserId();
        return entityManager.getReference(AppUser.class, userId);

    }
}
