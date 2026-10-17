package com.example.App.repository;

import com.example.App.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository  extends JpaRepository<Direccion,Long> {
}
