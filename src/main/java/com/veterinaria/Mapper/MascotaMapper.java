package com.veterinaria.Mapper;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Entity.Mascota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MascotaMapper {

    @Mapping(source = "duenio.id", target = "duenioId")
    @Mapping(source = "duenio.nombre", target = "duenioNombre")
    MascotaResponseDTO toResponseDto(Mascota mascota);

    @Mapping(target = "duenio", ignore = true)
    @Mapping(target = "turnos", ignore = true)
    Mascota toEntity(MascotaRequestDTO mascotaRequestDTO);

    List<MascotaResponseDTO> toResponseDtoList(List<Mascota> mascotas);
}
