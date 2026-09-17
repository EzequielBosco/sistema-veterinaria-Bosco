package com.veterinaria.Mapper;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;
import com.veterinaria.Entity.Duenio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DuenioMapper {

    DuenioResponseDTO toResponseDto(Duenio duenio);

    @Mapping(target = "mascotas", ignore = true)
    Duenio toEntity(DuenioRequestDTO duenioRequestDTO);

    List<DuenioResponseDTO> toResponseDtoList(List<Duenio> duenios);
}
