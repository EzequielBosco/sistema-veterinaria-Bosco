package com.veterinaria.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.veterinaria.Entity.enums.RolVeterinario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Entity
@Table(name = "participaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    @JsonBackReference("turno-participacion")
    private Turno turno;

    @ManyToOne
    @JoinColumn(name = "id_veterinario", nullable = false)
    @JsonBackReference("veterinario-participacion")
    private Veterinario veterinario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PRINCIPAL','ASISTENTE','CONSULTOR')")
    private RolVeterinario rol;
}
