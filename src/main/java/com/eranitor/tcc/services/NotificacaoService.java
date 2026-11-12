package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.NotificacaoDTO;
import com.eranitor.tcc.dto.TarefaResponseDTO;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProgressoService progressoService;

    @Autowired
    private PriorizacaoService priorizacaoService;

    public NotificacaoDTO gerarMensagemDiaria(Long usuarioId) {
        validarUsuario(usuarioId);

        Double desempenho = progressoService.calcularProgressoGeral(usuarioId);

        List<Tarefa> tarefasProximas = priorizacaoService.getTarefasProximas(usuarioId, 3);

        List<String> nomesTarefas = tarefasProximas.stream()
                .map(Tarefa::getTitulo)
                .collect(Collectors.toList());

        String mensagem = construirMensagem(desempenho, nomesTarefas);

        return new NotificacaoDTO(mensagem, desempenho, nomesTarefas);
    }

    public List<TarefaResponseDTO> buscarTarefasProximas(Long usuarioId) {
        validarUsuario(usuarioId);

        return priorizacaoService.getTarefasProximas(usuarioId, 3)
                .stream()
                .map(TarefaResponseDTO::from)
                .toList();
    }

    public Double buscarDesempenhoPercentual(Long usuarioId) {
        validarUsuario(usuarioId);

        return progressoService.calcularProgressoGeral(usuarioId);
    }

    public String construirMensagem(Double desempenho, List<String> tarefas) {
        String tarefasTexto = (tarefas == null || tarefas.isEmpty())
                ? "sem tarefas próximas"
                : String.join(", ", tarefas);

        return String.format("Você completou %.0f%% esta semana! Próximas: %s", desempenho, tarefasTexto);
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuário não encontrado");
        }
    }
}
