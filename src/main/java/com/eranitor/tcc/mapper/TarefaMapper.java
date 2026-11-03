package com.eranitor.tcc.mapper;

import com.eranitor.tcc.dto.TarefaAtualizacaoDTO;
import com.eranitor.tcc.dto.TarefaCreateDTO;
import com.eranitor.tcc.dto.TarefaDTO;
import com.eranitor.tcc.entity.Tarefa;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TarefaMapper {

    private final ModelMapper modelMapper;

    public TarefaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TarefaDTO toDTO(Tarefa tarefa) {
        Objects.requireNonNull(tarefa, "A tarefa não poder ser nula");
        return  modelMapper.map(tarefa, TarefaDTO.class);
    }

    public Tarefa toEntity (TarefaCreateDTO dto) {
        Objects.requireNonNull(dto, "O DTO de criação não pode ser nulo");
        return modelMapper.map(dto, Tarefa.class);
    }

    public Tarefa updateEntity(TarefaAtualizacaoDTO dto, Tarefa tarefa) {
        Objects.requireNonNull(dto, "O DTO de atualização não pode ser nulo");
        Objects.requireNonNull(tarefa, "A tarefa não pode ser nula");
        modelMapper.map(dto,tarefa);
        return tarefa;
    }
}
