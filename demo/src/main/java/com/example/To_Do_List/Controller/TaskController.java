package com.example.To_Do_List.Controller;


import com.example.To_Do_List.Service.TaskService;
import com.example.To_Do_List.dto.common.PageResponseDTO;
import com.example.To_Do_List.dto.task.TaskCreateDTO;
import com.example.To_Do_List.dto.task.TaskResponseDto;
import com.example.To_Do_List.dto.task.TaskStatsDTO;
import com.example.To_Do_List.dto.task.TaskUpdateDTO;
import com.example.To_Do_List.models.Prioridad;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @GetMapping("/tasks")
    public ResponseEntity<PageResponseDTO<TaskResponseDto>> allTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Prioridad prioridad,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(taskService.findAll(projectId, completed, prioridad, pageable), HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable long id) {
        return  new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDto> createTask(@Valid  @RequestBody TaskCreateDTO taskdto) {
        return  new ResponseEntity<>(taskService.create(taskdto),HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable long id, @Valid @RequestBody TaskUpdateDTO dto) {
        return new ResponseEntity<>(taskService.update(id, dto), HttpStatus.OK);
    }

    @PatchMapping("/tasks/{id}/toggle")
    public ResponseEntity<TaskResponseDto> markCompleted(@PathVariable long id) {
        return  new ResponseEntity<>(taskService.markAsCompleted(id),HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTasks(@PathVariable long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/stats")
    public  ResponseEntity<TaskStatsDTO> stats() {
        return new ResponseEntity<>(taskService.stats(),HttpStatus.OK);
    }
}
