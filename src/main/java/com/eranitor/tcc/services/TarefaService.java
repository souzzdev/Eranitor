package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.TarefaDTO;
import com.eranitor.tcc.dto.TarefaPriorizadaDTO;
import com.eranitor.tcc.dto.TarefaResponseDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.entity.Topico;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.infra.security.OwnershipService;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TarefaRepository;
import com.eranitor.tcc.repository.TopicoRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private PriorizacaoService priorizacaoService;

    @Autowired
    private ProgressoService progressoService;

    @Autowired
    private OwnershipService ownership;

    public List<TarefaPriorizadaDTO> getTarefasOrdenadas(Long usuarioId) {
        ownership.exigirUsuario(usuarioId);

        List<Tarefa> tarefas = priorizacaoService.getTarefasOrdenadas(usuarioId);

        Map<Long, Double> progressoPorMateria =
                progressoService.getProgressoPorMateria(usuarioId);

        return tarefas.stream()
                .map(tarefa -> {
                    Materia materia = tarefa.getMateria();

                    Double progresso = materia == null || materia.getIdMateria() == null
                            ? 0.0
                            : progressoPorMateria.getOrDefault(materia.getIdMateria(), 0.0) / 100.0;

                    Double prioridade = priorizacaoService.calcularPrioridade(
                            tarefa,
                            progresso
                    );

                    return new TarefaPriorizadaDTO(
                            tarefa.getIdTarefa(),
                            tarefa.getTitulo(),
                            tarefa.getDescricao(),
                            tarefa.getDataVencimento(),
                            tarefa.getCor(),
                            tarefa.getConcluida(),
                            prioridade,
                            materia != null ? materia.getIdMateria() : null,
                            materia != null ? materia.getNome() : null,
                            usuarioId
                    );
                })
                .collect(Collectors.toList());
    }

    public void createTarefa(Long usuarioId, Long materiaId, TarefaDTO dto) {
        ownership.exigirUsuario(usuarioId);

        if(tarefaRepository.existsByTituloIgnoreCaseAndMateria_IdMateriaAndUsuario_IdUsuario(
                dto.titulo(),
                materiaId,
                usuarioId
        )) {
            throw new RuntimeException("Você já possui uma tarefa com esse título nessa matéria.");
        }


        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada."));

        ownership.exigirDono(materia.getUsuario());


        Tarefa tarefa = new Tarefa();

        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setDataVencimento(dto.dataVencimento());
        tarefa.setCor(dto.cor());
        tarefa.setCriadoEm(LocalDate.now());
        tarefa.setConcluida(false);
        tarefa.setUsuario(usuario);
        tarefa.setMateria(materia);

        tarefaRepository.save(tarefa);

    }


    public void updateTarefa(Long id, TarefaDTO dto) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não  encontrada."));

        ownership.exigirDono(tarefa.getUsuario());

        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setDataVencimento(dto.dataVencimento());
        tarefa.setCor(dto.cor());

        if (dto.materiaId() != null) {
            Materia materia = materiaRepository.findById(dto.materiaId())
                    .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada."));

            ownership.exigirDono(materia.getUsuario());

            tarefa.setMateria(materia);
        }

        tarefaRepository.save(tarefa);

    }

    public void completarTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        ownership.exigirDono(tarefa.getUsuario());

        tarefa.setConcluida(true);

        tarefaRepository.save(tarefa);
    }

    public void reabrirTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        ownership.exigirDono(tarefa.getUsuario());

        tarefa.setConcluida(false);

        tarefaRepository.save(tarefa);
    }

    public void deleteTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));

        ownership.exigirDono(tarefa.getUsuario());

        tarefaRepository.deleteById(id);
    }

    public List<TarefaResponseDTO> getTarefasByMateria(Long materiaId) {
        materiaRepository.findById(materiaId)
                .ifPresent(materia -> ownership.exigirDono(materia.getUsuario()));

        return tarefaRepository.findByMateriaIdMateria(materiaId)
                .stream()
                .map(TarefaResponseDTO::from)
                .toList();
    }

    public List<TarefaResponseDTO> getTarefasByTopico(Long topicoId) {
        topicoRepository.findById(topicoId)
                .ifPresent(topico -> ownership.exigirDono(topico.getMateria() == null
                        ? null
                        : topico.getMateria().getUsuario()));

        return tarefaRepository.findByTopicoIdTopico(topicoId)
                .stream()
                .map(TarefaResponseDTO::from)
                .toList();
    }

    public List<TarefaResponseDTO> getTarefasByUsuario(Long usuarioId) {
        ownership.exigirUsuario(usuarioId);

        return tarefaRepository.findByUsuarioIdUsuario(usuarioId)
                .stream()
                .map(TarefaResponseDTO::from)
                .toList();
    }

    public Long geTarefasConcluidasByMaterias(Long materiaId, Boolean concluida) {
        return tarefaRepository.countByMateriaIdMateriaAndConcluida(materiaId, concluida);
    }

    public Long getTarefasTotaisByMateria(Long materiaId) {
        return tarefaRepository.countByMateriaIdMateria(materiaId);
    }


}
