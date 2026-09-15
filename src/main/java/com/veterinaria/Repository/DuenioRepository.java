package com.veterinaria.Repository;

import com.veterinaria.Entity.Duenio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DuenioRepository extends JpaRepository<Duenio, Long> {

    boolean existsByCedula(String cedula);

    boolean existsByEmail(String email);

    Optional<Duenio> findByCedula(String cedula);

    List<Duenio> findByNombreAndApellido(String nombre, String apellido);

    Optional<Duenio> findByEmail(String email);
}
