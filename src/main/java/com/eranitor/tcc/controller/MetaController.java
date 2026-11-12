package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.services.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meta")
public class MetaController {

    @Autowired
    private MetaService metaService;

    @PutMapping("/semana/{usuarioId}")
    public ResponseEntity<?> setMetaSemanal(
            @PathVariable Long usuarioId,
            @RequestParam Integer horas) {

        try {
            metaService.setMetaSemanal(usuarioId, horas);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/materia/{usuarioId}")
    public ResponseEntity<?> setMetaMateria(
            @PathVariable Long usuarioId,
            @RequestBody DesempenhoDTO dto) {

        try {
            metaService.setMetaMateria(
                    usuarioId,
                    dto.materiaId(),
                    dto.mes(),
                    dto.ano(),
                    dto.horasMeta()
            );
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }
}
