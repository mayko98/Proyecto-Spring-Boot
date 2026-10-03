package com.example.To_Do_List.dto.project;

import com.example.To_Do_List.models.Project;

public record ProjectResponseDTO(
        long id_project,
        String project_name,
        String project_description,
        String color
) {

    public static ProjectResponseDTO fromEntity(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getColor()
        );
    }
}
