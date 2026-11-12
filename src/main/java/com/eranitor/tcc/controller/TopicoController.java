package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.CreateTopicoDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.TopicoDTO;
import com.eranitor.tcc.dto.TopicoResponseDTO;
import com.eranitor.tcc.services.TopicoService;
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
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @PostMapping("/materia/{materiaId}")
    public ResponseEntity<?> criar(
            @PathVariable Long materiaId,
            @RequestBody @Valid CreateTopicoDTO dto) {

        try {
            topicoService.cadastrarTopico(dto, materiaId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}/materia/{materiaId}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @PathVariable Long materiaId,
            @RequestBody @Valid TopicoDTO dto) {

        try {
            topicoService.updateTopico(id, dto, materiaId);
            return ResponseEntity.ok().build();
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
            topicoService.deleteTopico(id);
            return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<TopicoResponseDTO>> listarPorMateria(
            @PathVariable Long materiaId) {

        return ResponseEntity.ok(
                topicoService.getTopicosByMateria(materiaId)
        );
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<?> marcarConcluido(
            @PathVariable Long id) {

        try {
            topicoService.marcarConcluido(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage()
                    ));
        }
    }
}
