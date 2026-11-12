package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.TarefaDTO;
import com.eranitor.tcc.dto.TarefaPriorizadaDTO;
import com.eranitor.tcc.dto.TarefaResponseDTO;
import com.eranitor.tcc.infra.exception.AcessoNegadoException;
import com.eranitor.tcc.services.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping("/priorizadas/{usuarioId}")
    public ResponseEntity<List<TarefaPriorizadaDTO>> getTarefasPriorizadas(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                tarefaService.getTarefasOrdenadas(usuarioId)
        );
    }

    @PostMapping("/{usuarioId}/materia/{materiaId}")
    public ResponseEntity<?> criar(
            @PathVariable Long usuarioId,
            @PathVariable Long materiaId,
            @RequestBody TarefaDTO dto) {

        try {
            tarefaService.createTarefa(usuarioId, materiaId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AcessoNegadoException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(
                            HttpStatus.CONFLICT.value(),
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody TarefaDTO dto) {

        try {
            tarefaService.updateTarefa(id, dto);
            return ResponseEntity.ok().build();
        } catch (AcessoNegadoException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<?> concluir(
            @PathVariable Long id) {

        try {
            tarefaService.completarTarefa(id);
            return ResponseEntity.ok().build();
        } catch (AcessoNegadoException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/{id}/reabrir")
    public ResponseEntity<?> reabrir(
            @PathVariable Long id) {

        try {
            tarefaService.reabrirTarefa(id);
            return ResponseEntity.ok().build();
        } catch (AcessoNegadoException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id) {

        try {
            tarefaService.deleteTarefa(id);
            return ResponseEntity.noContent().build();
        } catch (AcessoNegadoException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<TarefaResponseDTO>> listarPorMateria(
            @PathVariable Long materiaId) {

        return ResponseEntity.ok(
                tarefaService.getTarefasByMateria(materiaId)
        );
    }

    @GetMapping("/topico/{topicoId}")
    public ResponseEntity<List<TarefaResponseDTO>> listarPorTopico(
            @PathVariable Long topicoId) {

        return ResponseEntity.ok(
                tarefaService.getTarefasByTopico(topicoId)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TarefaResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                tarefaService.getTarefasByUsuario(usuarioId)
        );
    }
}
