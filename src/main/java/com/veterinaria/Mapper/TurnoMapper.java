package com.veterinaria.Mapper;

import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;
import com.veterinaria.Entity.Participacion;
import com.veterinaria.Entity.Turno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TurnoMapper {

    @Mapping(source = "mascota.id",     target = "mascotaId")
    @Mapping(source = "mascota.nombre", target = "mascotaNombre")
    @Mapping(target = "veterinarios",   expression = "java(obtenerVeterinarios(turno))")
    TurnoResponseDTO toDto(Turno turno);

    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "estado",         ignore = true)
    @Mapping(target = "mascota",        ignore = true)
    @Mapping(target = "participaciones",ignore = true)
    @Mapping(target = "prescripciones", ignore = true)
    Turno toEntity(TurnoRequestDTO turnoRequestDTO);

    List<TurnoResponseDTO> toDtoList(List<Turno> turnos);

    default List<TurnoVeterinarioResponseDTO> obtenerVeterinarios(Turno turno) {
        if (turno == null || turno.getParticipaciones() == null) {
            return List.of();
        }

        return turno.getParticipaciones().stream()
                .sorted(Comparator
                        .comparing(Participacion::getRol)
                        .thenComparing(participacion -> participacion.getVeterinario().getId()))
                .map(participacion -> new TurnoVeterinarioResponseDTO(
                        participacion.getVeterinario().getId(),
                        formatearNombreVeterinario(participacion),
                        participacion.getRol()))
                .toList();
    }

    default String formatearNombreVeterinario(Participacion participacion) {
        if (participacion.getVeterinario() == null) {
            return null;
        }
        String nombre = participacion.getVeterinario().getNombre();
        String apellido = participacion.getVeterinario().getApellido();
        if (apellido == null || apellido.isBlank()) {
            return nombre;
        }
        return nombre + " " + apellido;
    }
}
