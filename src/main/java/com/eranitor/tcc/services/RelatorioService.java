package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.dto.RelatorioDTO;
import com.eranitor.tcc.dto.TempoEstudoDTO;
import com.eranitor.tcc.entity.Agenda;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.TempoEstudo;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.AgendaRepository;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.TempoEstudoRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DesempenhoRepository desempenhoRepository;

    @Autowired
    private TempoEstudoRepository tempoEstudoRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    public RelatorioDTO gerarRelatorioPeriodo(Long usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        validarUsuario(usuarioId);

        List<DesempenhoDTO> desempenho = desempenhoRepository.findByUsuario_IdUsuarioAndMesAndAno(
                usuarioId, dataInicio.getMonthValue(), dataInicio.getYear()
        ).stream()
                .map(this::toDesempenhoDTO)
                .collect(Collectors.toList());

        List<TempoEstudoDTO> tempoEstudo = tempoEstudoRepository.findByUsuario_IdUsuarioAndMesAndAno(
                usuarioId, dataInicio.getMonthValue(), dataInicio.getYear()
        ).stream()
                .map(this::toTempoEstudoDTO)
                .collect(Collectors.toList());

        List<AgendaDTO> eventos = agendaRepository.findByUsuario_IdUsuarioAndDataBetween(
                usuarioId, dataInicio, dataFim
        ).stream()
                .map(this::toAgendaDTO)
                .collect(Collectors.toList());

        String periodo = dataInicio.toString() + " a " + dataFim.toString();

        return new RelatorioDTO(periodo, desempenho, tempoEstudo, eventos);
    }

    public RelatorioDTO gerarRelatorioMateria(Long materiaId, LocalDate mesInicio, LocalDate mesFim) {
        List<DesempenhoDTO> desempenho = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(
                materiaId, mesInicio.getMonthValue(), mesInicio.getYear()
        ).stream()
                .map(this::toDesempenhoDTO)
                .collect(Collectors.toList());

        List<TempoEstudoDTO> tempoEstudo = tempoEstudoRepository.findByMateria_IdMateriaAndMesAndAno(
                materiaId, mesInicio.getMonthValue(), mesInicio.getYear()
        ).stream()
                .map(this::toTempoEstudoDTO)
                .collect(Collectors.toList());

        List<AgendaDTO> eventos = List.of();

        String periodo = mesInicio.toString() + " a " + mesFim.toString();

        return new RelatorioDTO(periodo, desempenho, tempoEstudo, eventos);
    }

    public RelatorioDTO gerarRelatorioTempoEstudo(Long usuarioId, Integer mes, Integer ano) {
        validarUsuario(usuarioId);

        List<TempoEstudoDTO> tempoEstudo = tempoEstudoRepository.findByUsuario_IdUsuarioAndMesAndAno(
                usuarioId, mes, ano
        ).stream()
                .map(this::toTempoEstudoDTO)
                .collect(Collectors.toList());

        String periodo = mes + "/" + ano;

        return new RelatorioDTO(periodo, List.of(), tempoEstudo, List.of());
    }

    public RelatorioDTO exportarJSON(RelatorioDTO relatorio) {
        return relatorio;
    }

    private DesempenhoDTO toDesempenhoDTO(Desempenho d) {
        return new DesempenhoDTO(
                d.getIdDesempenho(),
                d.getUsuario().getIdUsuario(),
                d.getMateria().getIdMateria(),
                d.getMes(),
                d.getAno(),
                d.getHorasConcluidas(),
                d.getHorasMeta()
        );
    }

    private TempoEstudoDTO toTempoEstudoDTO(TempoEstudo t) {
        return new TempoEstudoDTO(
                t.getIdTempoEstudo(),
                t.getUsuario().getIdUsuario(),
                t.getMateria().getIdMateria(),
                t.getMes(),
                t.getAno(),
                t.getMinutosAtividade(),
                t.getMinutosVideo(),
                t.getMinutosApostila()
        );
    }

    private AgendaDTO toAgendaDTO(Agenda a) {
        return new AgendaDTO(
                a.getId(),
                a.getUsuario().getIdUsuario(),
                a.getData(),
                a.getTitulo(),
                a.getTipo()
        );
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuário não encontrado");
        }
    }
}
