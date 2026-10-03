package com.example.To_Do_List.Service;

import com.example.To_Do_List.dto.common.PageResponseDTO;
import com.example.To_Do_List.dto.task.TaskCreateDTO;
import com.example.To_Do_List.dto.task.TaskResponseDto;
import com.example.To_Do_List.dto.task.TaskStatsDTO;
import com.example.To_Do_List.dto.task.TaskUpdateDTO;
import com.example.To_Do_List.exception.ResourceNotFoundException;
import com.example.To_Do_List.models.Prioridad;
import com.example.To_Do_List.models.Project;
import com.example.To_Do_List.models.Task;
import com.example.To_Do_List.repository.TaskRepository;
import com.example.To_Do_List.specification.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;


    // En TaskService
    public PageResponseDTO<TaskResponseDto> findAll(Long projectId, Boolean completed, Prioridad prioridad, Pageable pageable) {

        Specification<Task> spec = Specification.where(TaskSpecifications.hasProjectId(projectId))
                .and(TaskSpecifications.isCompleted(completed))
                .and(TaskSpecifications.hasPrioridad(prioridad));

        return PageResponseDTO.fromPage(taskRepository.findAll(spec, pageable).map(TaskResponseDto::fromEntity));
    }

    public TaskResponseDto findById(long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found id " + id));
       return TaskResponseDto.fromEntity(task);
    }
    public TaskResponseDto create(TaskCreateDTO dto) {
        Project project;
        if (dto.projectId() != null) {
            project = projectService.findEntityById(dto.projectId());

        }else {
            project = projectService.getOrCreateInbox();
        }
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPrioridad(dto.prioridad()); // Si viene null, @PrePersist le asignará MEDIA automáticamente
        task.setCompletedAt(null);
        task.setProject(project);
        taskRepository.save(task);
        return TaskResponseDto.fromEntity(task);
    }
    public TaskResponseDto update(long id , TaskUpdateDTO task) {
        Task currentTask = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Task not found  con id "+id));

        currentTask.setTitle(task.title());
        currentTask.setDescription(task.description());
        currentTask.setPrioridad(task.prioridad());
        taskRepository.save(currentTask);
        return TaskResponseDto.fromEntity(currentTask);
    }

    public TaskResponseDto markAsCompleted(long id) {
        Task task = taskRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Task not found id "+id));
        task.setCompletedAt(task.isCompleted() ? null : LocalDateTime.now());
        taskRepository.save(task);

        return TaskResponseDto.fromEntity(task);
    }

    public void deleteById(long id) {
        Task task = taskRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Task not found id "+id));

        taskRepository.delete(task);

    }

    public List<TaskResponseDto> findByProjectId(Long id,Boolean completed, Prioridad prioridad) {
        projectService.findEntityById(id);

        Specification<Task> spec = Specification.where(TaskSpecifications.hasProjectId(id))
                .and(TaskSpecifications.isCompleted(completed)).and(TaskSpecifications.hasPrioridad(prioridad));
        return taskRepository.findAll(spec).stream().map(TaskResponseDto::fromEntity).toList();
    }


    public TaskStatsDTO stats() {
        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByCompletedAtIsNull();

        long completedTasks = taskRepository.countByCompletedAtAfter(LocalDate.now().atStartOfDay());

        return new TaskStatsDTO(totalTasks,pendingTasks,(totalTasks-pendingTasks),completedTasks);
    }
}
