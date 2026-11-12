package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.MateriaDTO;
import com.eranitor.tcc.dto.MateriaResponseDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.services.MateriaService;
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
@RequestMapping("/materias")
public class MateriaController {
    @Autowired
    private MateriaService materiaService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> criar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid MateriaDTO dto) {

        try {
            materiaService.cadastrarMateria(dto, usuarioId);
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

    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @RequestBody @Valid MateriaDTO dto) {

        try {
            MateriaResponseDTO resposta = materiaService.updateMateria(dto, usuarioId, id);
            return ResponseEntity.ok(resposta);
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
            @PathVariable Long id) {

        try {
            materiaService.deleteMateria(id);
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

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<com.eranitor.tcc.entity.Materia>> listar(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                materiaService.getTodasMaterias(usuarioId)
        );
    }

    @GetMapping("/usuario/{usuarioId}/ativas")
    public ResponseEntity<List<com.eranitor.tcc.entity.Materia>> listarAtivas(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                materiaService.getMateriasAtivas(usuarioId)
        );
    }
}
