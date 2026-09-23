package com.eranitor.tcc.mapper;

import com.eranitor.tcc.dto.MateriaDTO;
import com.eranitor.tcc.dto.MateriaResponseDTO;
import com.eranitor.tcc.entity.Materia;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MateriaMapper {

    private final ModelMapper modelMapper;

    public MateriaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Materia toEntity(MateriaDTO dto) {
        Objects.requireNonNull(dto, "O DTO de cadastro não pode ser nulo.");

        Materia materia = modelMapper.map(dto, Materia.class);

        return materia;
    }

    public Materia updateEntity(MateriaDTO dto, Materia materia) {
        Objects.requireNonNull(dto, "O DTO de atualização não pode ser nulo.");
        Objects.requireNonNull(materia, "A matéria não pode ser nula.");

        modelMapper.map(dto, materia);

        return materia;
    }

    public MateriaResponseDTO toResponseDTO(Materia materia) {
        Objects.requireNonNull(materia, "A matéria não pode ser nula.");

        return modelMapper.map(materia, MateriaResponseDTO.class);
    }
}
