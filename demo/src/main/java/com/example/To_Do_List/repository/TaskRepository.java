package com.example.To_Do_List.repository;

import com.example.To_Do_List.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    long countByCompletedAtIsNull();
    long countByCompletedAtIsNotNull();
    long countByCompletedAtAfter(LocalDateTime d);
}
