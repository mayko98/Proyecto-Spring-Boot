package com.example.To_Do_List.dto.task;

public record TaskStatsDTO(
        long totalTasks,
        long pendingTasks,
        long completedTasks,
        long completedToday
) {
}
