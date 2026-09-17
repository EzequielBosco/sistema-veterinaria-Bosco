package com.veterinaria.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "duenios")
@Getter
@Setter
@NoArgsConstructor
public class Duenio extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_duenio")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String cedula;

    @OneToMany(mappedBy = "duenio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();

    public Duenio(String nombre, String apellido, String telefono, String email, String cedula) {
        super(nombre, apellido, telefono, email);
        this.cedula = cedula;
    }
}
