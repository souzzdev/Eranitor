package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.NotificacaoDTO;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.services.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/dia/{usuarioId}")
    public ResponseEntity<NotificacaoDTO> mensagemDiaria(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                notificacaoService.gerarMensagemDiaria(usuarioId)
        );
    }

    @GetMapping("/tarefas-proximas/{usuarioId}")
    public ResponseEntity<List<Tarefa>> tarefasProximas(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                notificacaoService.buscarTarefasProximas(usuarioId)
        );
    }

    @GetMapping("/desempenho/{usuarioId}")
    public ResponseEntity<Double> desempenho(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                notificacaoService.buscarDesempenhoPercentual(usuarioId)
        );
    }
}
