package com.veterinaria.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class Persona {

    @Column(nullable = false, length = 100)
    protected String nombre;

    @Column(nullable = false, length = 100)
    protected String apellido;

    @Column(length = 30)
    protected String telefono;

    @Column(length = 150, unique = true)
    private String email;

    protected Persona(String nombre, String apellido, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }
}