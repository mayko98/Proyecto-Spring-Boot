package com.example.To_Do_List.Controller;

import com.example.To_Do_List.Service.ProjectService;
import com.example.To_Do_List.Service.TaskService;
import com.example.To_Do_List.dto.project.ProjectCreateDTO;
import com.example.To_Do_List.dto.project.ProjectResponseDTO;
import com.example.To_Do_List.dto.project.ProjectUpdateDTO;
import com.example.To_Do_List.dto.task.TaskResponseDto;
import com.example.To_Do_List.models.Prioridad;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;


    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponseDTO>> allProjects() {
        return new ResponseEntity<>(projectService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/projects/{id}/tasks")
    public ResponseEntity<List<TaskResponseDto>> getProjectTask(@PathVariable("id") long idproyecto,
                                                                @RequestParam(required = false) Boolean completed,
                                                                @RequestParam(required = false) Prioridad prioridad){
        return new ResponseEntity<>(taskService.findByProjectId(idproyecto,completed,prioridad), HttpStatus.OK);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable long id){
        return new ResponseEntity<>(projectService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectCreateDTO project) {
        return  new ResponseEntity<>(projectService.create(project),HttpStatus.CREATED);
    }


    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable long id, @Valid @RequestBody ProjectUpdateDTO project) {
        return new ResponseEntity<>(projectService.update(id, project), HttpStatus.OK);
    }


    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
