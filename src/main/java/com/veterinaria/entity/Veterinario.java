package com.veterinaria.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Veterinario extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veterinario")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @Column(length = 100)
    private String especialidad;

    @ManyToMany(mappedBy = "veterinarios")
    @JsonBackReference("turno-veterinario")
    private Set<Turno> turnos = new HashSet<>();

    public Veterinario(String nombre, String apellido, String telefono, String email, String matricula, String especialidad) {
        super(nombre, apellido, telefono, email);
        this.matricula = matricula;
        this.especialidad = especialidad;
    }
}