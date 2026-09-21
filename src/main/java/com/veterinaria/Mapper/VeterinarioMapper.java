package com.veterinaria.Mapper;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.Entity.Veterinario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VeterinarioMapper {

    VeterinarioResponseDTO toResponseDto(Veterinario veterinario);

    @Mapping(target = "participaciones", ignore = true)
    Veterinario toEntity(VeterinarioRequestDTO veterinarioRequestDTO);

    List<VeterinarioResponseDTO> toResponseDtoList(List<Veterinario> veterinarios);
}
