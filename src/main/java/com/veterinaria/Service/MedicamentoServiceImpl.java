package com.veterinaria.Service;

import com.veterinaria.DTO.MedicamentoRequestDTO;
import com.veterinaria.DTO.MedicamentoResponseDTO;
import com.veterinaria.Entity.Medicamento;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.MedicamentoMapper;
import com.veterinaria.Repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoMapper medicamentoMapper;

    public MedicamentoServiceImpl(
            MedicamentoRepository medicamentoRepository,
            MedicamentoMapper medicamentoMapper) {
        this.medicamentoRepository = medicamentoRepository;
        this.medicamentoMapper = medicamentoMapper;
    }

    @Override
    public List<MedicamentoResponseDTO> getAllMedicamentos() {
        return medicamentoMapper.toResponseDtoList(medicamentoRepository.findAll());
    }

    @Override
    public MedicamentoResponseDTO getMedicamentoById(Long id) {
        return medicamentoMapper.toResponseDto(obtenerMedicamento(id));
    }

    @Override
    public MedicamentoResponseDTO createMedicamento(MedicamentoRequestDTO dto) {
        if (medicamentoRepository.existsByNombreAndPrincipioActivo(dto.getNombre(), dto.getPrincipioActivo())) {
            throw new DuplicateResourceException("Ya existe un medicamento con ese nombre y principio activo");
        }
        return medicamentoMapper.toResponseDto(medicamentoRepository.save(medicamentoMapper.toEntity(dto)));
    }

    @Override
    public MedicamentoResponseDTO updateMedicamento(Long id, MedicamentoRequestDTO dto) {
        Medicamento medicamento = obtenerMedicamento(id);

        boolean nombreCambio = !medicamento.getNombre().equals(dto.getNombre());
        boolean principioCambio = !medicamento.getPrincipioActivo().equals(dto.getPrincipioActivo());

        if ((nombreCambio || principioCambio)
                && medicamentoRepository.existsByNombreAndPrincipioActivo(dto.getNombre(), dto.getPrincipioActivo())) {
            throw new DuplicateResourceException("Ya existe un medicamento con ese nombre y principio activo");
        }

        medicamento.setNombre(dto.getNombre());
        medicamento.setPrincipioActivo(dto.getPrincipioActivo());
        medicamento.setStock(dto.getStock());
        medicamento.setPrecioUnitario(dto.getPrecioUnitario());

        return medicamentoMapper.toResponseDto(medicamentoRepository.save(medicamento));
    }

    @Override
    public void deleteMedicamento(Long id) {
        medicamentoRepository.delete(obtenerMedicamento(id));
    }

    private Medicamento obtenerMedicamento(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un medicamento con id " + id));
    }
}
