package com.example.To_Do_List.Service;

import com.example.To_Do_List.dto.project.ProjectCreateDTO;
import com.example.To_Do_List.dto.project.ProjectResponseDTO;
import com.example.To_Do_List.dto.project.ProjectUpdateDTO;
import com.example.To_Do_List.exception.ResourceNotFoundException;
import com.example.To_Do_List.models.Project;
import com.example.To_Do_List.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    public List<ProjectResponseDTO> findAll() {
        return projectRepository.findAll().stream().map(ProjectResponseDTO::fromEntity).toList();
    }
    public ProjectResponseDTO findById(Long id) {

        return ProjectResponseDTO.fromEntity(findEntityById(id));
    }
    // 1. Para uso interno (devuelve la Entidad Project):
    public Project findEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found id " + id));
    }
    public ProjectResponseDTO create(ProjectCreateDTO project) {
        Project project1 = new Project();
        project1.setName(project.name());
        project1.setDescription(project.description());

        // Si no envía color, le asignamos gris por defecto como Todoist
        String colorDefault = (project.color() != null && !project.color().isBlank())
                ? project.color()
                : "#808080";
        project1.setColor(colorDefault);
        return  ProjectResponseDTO.fromEntity(projectRepository.save(project1));
    }

    public ProjectResponseDTO update(long id, ProjectUpdateDTO projectnuevo) {
        Project project = findEntityById(id);
        project.setName(projectnuevo.name());
        project.setDescription(projectnuevo.description());
        project.setColor(projectnuevo.color());
        return ProjectResponseDTO.fromEntity(projectRepository.save(project));
    }

    public void delete(long id) {
        Project project = findEntityById(id);
        projectRepository.delete(project);
    }

    public Project getOrCreateInbox() {
        return projectRepository.findByName("Inbox")
                .orElseGet(() -> projectRepository.save(new Project(null, "Inbox", "Bandeja de entrada", null, "#808080")));
    }



}
