package com.example.App.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//@EntityListeners(AuditingEntityListener.class)
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100,nullable = false)
    private String nombre;

    @Column(length = 500,nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock = 0;

    @Column(name = "codigo_referencia", nullable = false, unique = true, length = 50, updatable = false)
    private String codigoReferencia;

    @Column(name = "fecha_registro")
    private LocalDateTime fecha;

    @Builder.Default
    @Column(name = "producto_activo")
    private boolean activo =  true;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;


    @ManyToMany
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<Categoria>();

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}
