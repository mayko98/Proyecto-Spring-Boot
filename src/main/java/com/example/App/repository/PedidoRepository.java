package com.example.App.repository;

import com.example.App.model.Pedido;
import com.example.App.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findByUsuarioId(Long id);
}
