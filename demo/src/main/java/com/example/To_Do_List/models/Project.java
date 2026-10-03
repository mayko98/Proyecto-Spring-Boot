package com.example.To_Do_List.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,length = 100)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El título no puede superar los 50 caracteres")
    private String name;

    @Column(length = 200)
    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String description;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks ;

    @Column(length = 7,nullable = false)
    @Size(max = 7, message = "El color debe ser un código hexadecimal válido (ej: #FF5733)")
    private String color = "#808080";
}
