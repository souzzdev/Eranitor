package com.eranitor.tcc.mapper;

import com.eranitor.tcc.dto.AgendaCreateDTO;
import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.entity.Agenda;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AgendaMapper {

    private final ModelMapper modelMapper;

    public AgendaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Agenda toEntity(AgendaCreateDTO dto) {
        Objects.requireNonNull(dto, "O DTO de criação não pode ser nulo");

        Agenda agenda = new Agenda();
        agenda.setData(dto.data());
        agenda.setTitulo(dto.titulo());
        agenda.setTipo(dto.tipo());

        return agenda;
    }

    public Agenda updateEntity(AgendaCreateDTO dto, Agenda agenda) {
        Objects.requireNonNull(dto, "O DTO de atualização não pode ser nulo");
        Objects.requireNonNull(agenda, "A agenda não pode ser nula");

        agenda.setData(dto.data());
        agenda.setTitulo(dto.titulo());
        agenda.setTipo(dto.tipo());

        return agenda;
    }

    public AgendaDTO toDTO(Agenda agenda) {
        Objects.requireNonNull(agenda, "A agenda não pode ser nula");

        return new AgendaDTO(
                agenda.getId(),
                agenda.getUsuario().getIdUsuario(),
                agenda.getData(),
                agenda.getTitulo(),
                agenda.getTipo()
        );
    }
}
