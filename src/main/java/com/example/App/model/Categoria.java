package com.example.App.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 50,unique = true)
    private String nombre;

    @Column(nullable = false,length = 200)
    private String descripcion;

    @ManyToMany(mappedBy = "categorias")
    @Builder.Default
    private Set<Producto> productos =  new HashSet<Producto>();
}
