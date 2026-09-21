package com.veterinaria.Repository;

import com.veterinaria.Entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    boolean existsByMatricula(String matricula);

    boolean existsByEmail(String email);
}
