package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesempenhoService {
    private final DesempenhoRepository desempenhoRepository;
    private final MateriaRepository materiaRepository;
    private final TempoEstudoService tempoEstudoService;
    private final UsuarioRepository usuarioRepository;

    public DesempenhoService(DesempenhoRepository desempenhoRepository, MateriaRepository materiaRepository, TempoEstudoService tempoEstudoService, UsuarioRepository usuarioRepository) {
        this.desempenhoRepository = desempenhoRepository;
        this.materiaRepository = materiaRepository;
        this.tempoEstudoService = tempoEstudoService;
        this.usuarioRepository = usuarioRepository;
    }

    public DesempenhoDTO calcularDesempenho(Long usuarioId, Long materiaId, Integer mes, Integer ano) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        if (materiaId == null) {
            throw new IllegalArgumentException("A matéria é obrigatória");
        }

        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve estar entre 1 e 12");
        }

        if (ano == null || ano < 1) {
            throw new IllegalArgumentException("O ano deve ser válido");
        }

        List<Desempenho> registros = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(materiaId, mes, ano);

        Integer horasConcluidas = 0;
        Integer horasMeta = 0;

        for (Desempenho d : registros) {
            if (d.getHorasConcluidas() != null) horasConcluidas += d.getHorasConcluidas();
            if (d.getHorasMeta() != null) horasMeta += d.getHorasMeta();
        }

        return new DesempenhoDTO(
                null,
                usuarioId,
                materiaId,
                mes,
                ano,
                horasConcluidas,
                horasMeta
        );
    }

    public double calcularProgresso(Integer horasConcluidas, Integer horasMeta) {
        if (horasMeta == null || horasMeta <= 0) {
            return 0.0;
        }

        return (horasConcluidas == null ? 0.0 : horasConcluidas) / horasMeta * 100.0;
    }

    public DesempenhoDTO registrarDesempenho(Long usuarioId, Long materiaId, Integer mes, Integer ano, Integer horasConcluidas) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        if (materiaId == null) {
            throw new IllegalArgumentException("A matéria é obrigatória");
        }

        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve estar entre 1 e 12");
        }

        if (ano == null || ano < 1) {
            throw new IllegalArgumentException("O ano deve ser válido");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        List<Desempenho> existentes = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(
                materiaId, mes, ano
        );

        Desempenho desempenho;
        if (!existentes.isEmpty()) {
            desempenho = existentes.get(0);
            desempenho.setHorasConcluidas(
                    (desempenho.getHorasConcluidas() == null ? 0 : desempenho.getHorasConcluidas()) + horasConcluidas
            );
        } else {
            desempenho = new Desempenho();
            desempenho.setUsuario(usuario);
            desempenho.setMateria(materia);
            desempenho.setMes(mes);
            desempenho.setAno(ano);
            desempenho.setHorasConcluidas(horasConcluidas);
            desempenho.setHorasMeta(0);
        }

        Desempenho salvo = desempenhoRepository.save(desempenho);

        return new DesempenhoDTO(
                salvo.getIdDesempenho(),
                salvo.getUsuario().getIdUsuario(),
                salvo.getMateria().getIdMateria(),
                salvo.getMes(),
                salvo.getAno(),
                salvo.getHorasConcluidas(),
                salvo.getHorasMeta()
        );
    }

    public List<DesempenhoDTO> getDesempenhoUsuario(Long usuarioId, Integer mes, Integer ano) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        List<Desempenho> registros = desempenhoRepository.findByUsuario_IdUsuarioAndMesAndAno(
                usuarioId, mes, ano
        );

        return registros.stream()
                .map(d -> new DesempenhoDTO(
                        d.getIdDesempenho(),
                        d.getUsuario().getIdUsuario(),
                        d.getMateria().getIdMateria(),
                        d.getMes(),
                        d.getAno(),
                        d.getHorasConcluidas(),
                        d.getHorasMeta()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void setMetaSemanal(Long usuarioId, Long materiaId, Integer mes, Integer ano, Integer horasMeta) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        if (materiaId == null) {
            throw new IllegalArgumentException("A matéria é obrigatória");
        }

        if (horasMeta == null || horasMeta <= 0) {
            throw new IllegalArgumentException("As horas de meta devem ser maiores que zero");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        List<Desempenho> existentes = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(
                materiaId, mes, ano
        );

        Desempenho desempenho;
        if (!existentes.isEmpty()) {
            desempenho = existentes.get(0);
        } else {
            desempenho = new Desempenho();
            desempenho.setUsuario(usuario);
            desempenho.setMateria(materia);
            desempenho.setMes(mes);
            desempenho.setAno(ano);
            desempenho.setHorasConcluidas(0);
        }

        desempenho.setHorasMeta(horasMeta);
        desempenhoRepository.save(desempenho);
    }
}
