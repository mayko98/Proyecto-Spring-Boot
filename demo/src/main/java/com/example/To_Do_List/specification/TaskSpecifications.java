package com.example.To_Do_List.specification;

import com.example.To_Do_List.models.Prioridad;
import com.example.To_Do_List.models.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {

    public static Specification<Task> hasPrioridad(Prioridad prioridad) {
        return (root,query,cb)->{
          if(prioridad == null) return null;
          return cb.equal(root.get("prioridad"), prioridad);
        };
    }


   public static Specification<Task> hasProjectId(Long projectId) {
       return (root,query,cb)->{
           if(projectId == null) return null;
           return cb.equal(root.get("project").get("id"), projectId);
       };
   }

    public static Specification<Task> isCompleted(Boolean completed) {
        return (root,query,cb)->{
            if(completed == null) return null;
            return completed
                    ? cb.isNotNull(root.get("completedAt"))
                    : cb.isNull(root.get("completedAt"));
        };
    }
}
