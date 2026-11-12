package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.TempoEstudoRegistroDTO;
import com.eranitor.tcc.dto.TempoEstudoDTO;
import com.eranitor.tcc.services.TempoEstudoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tempos-estudo")
public class TempoEstudoController {

    @Autowired
    private TempoEstudoService tempoEstudoService;

    @PostMapping("/{usuarioId}")
    public ResponseEntity<?> registrar(
            @PathVariable Long usuarioId,
            @RequestBody TempoEstudoRegistroDTO dto) {

        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(tempoEstudoService.registrarTempo(dto, usuarioId));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TempoEstudoDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                tempoEstudoService.listarPorUsuario(usuarioId)
        );
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<TempoEstudoDTO>> listarPorMateria(
            @PathVariable Long materiaId) {

        return ResponseEntity.ok(
                tempoEstudoService.listarPorTarefa(materiaId)
        );
    }
}
