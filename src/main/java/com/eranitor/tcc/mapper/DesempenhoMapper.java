package com.eranitor.tcc.mapper;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.entity.Desempenho;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DesempenhoMapper {

    private ModelMapper modelMapper;

    public DesempenhoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DesempenhoDTO toDTO (Desempenho desempenho) {
        Objects.requireNonNull(desempenho, "O desempenho não pode ser nulo");

        return new DesempenhoDTO(
                desempenho.getIdDesempenho(),
                desempenho.getUsuario().getIdUsuario(),
                desempenho.getMateria().getIdMateria(),
                desempenho.getMes(),
                desempenho.getAno(),
                desempenho.getHorasConcluidas(),
                desempenho.getHorasMeta()
        );
    }

    public Desempenho toEntity(DesempenhoDTO dto) {
        Objects.requireNonNull(dto, "O DTO de desempenho não pode ser nulo");

        Desempenho desempenho = new Desempenho();

        desempenho.setMes(dto.mes());
        desempenho.setAno(dto.ano());
        desempenho.setHorasConcluidas(dto.horasConcluidas());
        desempenho.setHorasMeta(dto.horasMeta());

        return desempenho;
    }
}
