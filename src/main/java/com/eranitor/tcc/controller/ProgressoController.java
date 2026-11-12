package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.ProgressoDTO;
import com.eranitor.tcc.services.ProgressoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/progresso")
public class ProgressoController {

    @Autowired
    private ProgressoService progressoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Double> getProgressoGeral(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                progressoService.calcularProgressoGeral(usuarioId)
        );
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<Double> getProgressoMateria(
            @PathVariable Long materiaId) {

        return ResponseEntity.ok(
                progressoService.calcularProgressoMateria(materiaId)
        );
    }

    @GetMapping("/usuario/{usuarioId}/materias")
    public ResponseEntity<Map<Long, Double>> getProgressoPorMateria(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                progressoService.getProgressoPorMateria(usuarioId)
        );
    }
}
