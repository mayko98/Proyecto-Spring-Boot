package com.example.App.repository;

import com.example.App.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {

        Optional<Producto> findByCodigoReferencia(String codigoReferee);

        boolean existsByCodigoReferencia(String codigoReferencia);

}
