package com.example.App.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario",nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direccion_Envio",nullable = false)
    private Direccion direccionEnvio;


    private LocalDateTime fechaPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DetallePedido> detallePedidoList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido", nullable = false)
    private EstadoPedido estadoPedido = EstadoPedido.PENDIENTE;
    private BigDecimal total;

    @PrePersist
    public void prePersist() {
        if (this.fechaPedido == null) {
            this.fechaPedido = LocalDateTime.now();
        }
    }
    public void addDetalle(DetallePedido detalle) {
        this.detallePedidoList.add(detalle);
        detalle.setPedido(this); // <-- La propia entidad se encarga de que nunca se olvide
    }

    public void removeDetalle(DetallePedido detalle) {
        this.detallePedidoList.remove(detalle);
        detalle.setPedido(null);
    }

    public void calcularTotal() {
        this.total = this.detallePedidoList.stream()
                .map(linea->linea.getPrecioUnitario().multiply(BigDecimal.valueOf(linea.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
