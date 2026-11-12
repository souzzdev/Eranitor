package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AgendaCreateDTO;
import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.services.AgendaService;
import jakarta.validation.Valid;
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
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping("/{usuarioId}")
    public ResponseEntity<?> criar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid AgendaCreateDTO dto) {

        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(agendaService.criarAgenda(dto, usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AgendaDTO>> listar(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                agendaService.listarPorUsuario(usuarioId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid AgendaCreateDTO dto) {

        try {
            return ResponseEntity.ok(
                    agendaService.atualizarAgenda(id, dto)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Integer id) {

        try {
            agendaService.deletarAgenda(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }
}
