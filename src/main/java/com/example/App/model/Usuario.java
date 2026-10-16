package com.example.App.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nombre;
    @Column(length = 50, nullable = false)
    private String apellido;
    @Column(length = 50, nullable = false,unique = true)
    private String email;
    @Column(length = 200, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50, nullable = false)
    private Tipo tipo;
    private LocalDate fechaNacimiento;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos = new ArrayList<>();
}
