package com.veterinaria.Repository;

import com.veterinaria.Entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNombreAndPrincipioActivo(String nombre, String principioActivo);
}
