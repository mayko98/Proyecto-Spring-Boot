package com.example.To_Do_List.dto.task;

import com.example.To_Do_List.models.Prioridad;
import com.example.To_Do_List.models.Task;

import java.time.LocalDateTime;

public record TaskResponseDto(
        long id_task,
        String task_name,
        String task_description,
        Prioridad prioridad,
        LocalDateTime createdAt,
        LocalDateTime CompleteAt,
        boolean completed,
        long id_project,
        String project_name


        ) {

    public static TaskResponseDto fromEntity(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPrioridad(),
                task.getCreatedAt(),
                task.getCompletedAt(),
                task.isCompleted(),
                task.getProject().getId(),
                task.getProject().getName()
        );


    }
}
