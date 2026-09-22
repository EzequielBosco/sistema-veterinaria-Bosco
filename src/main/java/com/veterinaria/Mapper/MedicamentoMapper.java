package com.veterinaria.Mapper;

import com.veterinaria.DTO.MedicamentoRequestDTO;
import com.veterinaria.DTO.MedicamentoResponseDTO;
import com.veterinaria.Entity.Medicamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicamentoMapper {

    MedicamentoResponseDTO toResponseDto(Medicamento medicamento);

    @Mapping(target = "id", ignore = true)
    Medicamento toEntity(MedicamentoRequestDTO dto);

    List<MedicamentoResponseDTO> toResponseDtoList(List<Medicamento> medicamentos);
}
