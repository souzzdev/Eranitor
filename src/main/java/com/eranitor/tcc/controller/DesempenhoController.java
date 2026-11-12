package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.infra.exception.AcessoNegadoException;
import com.eranitor.tcc.services.DesempenhoService;
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
@RequestMapping("/desempenho")
public class DesempenhoController {

    @Autowired
    private DesempenhoService desempenhoService;

    @PostMapping
    public ResponseEntity<?> registrar(
            @RequestBody DesempenhoDTO dto) {

        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(desempenhoService.registrarDesempenho(
                            dto.usuarioId(),
                            dto.materiaId(),
                            dto.mes(),
                            dto.ano(),
                            dto.horasConcluidas()
                    ));
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

    @GetMapping("/usuario/{usuarioId}/materia/{materiaId}/periodo/{mes}/{ano}")
    public ResponseEntity<DesempenhoDTO> buscarPorPeriodo(
            @PathVariable Long usuarioId,
            @PathVariable Long materiaId,
            @PathVariable Integer mes,
            @PathVariable Integer ano) {

        return ResponseEntity.ok(
                desempenhoService.calcularDesempenho(usuarioId, materiaId, mes, ano)
        );
    }

    @GetMapping("/usuario/{usuarioId}/periodo/{mes}/{ano}")
    public ResponseEntity<List<DesempenhoDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Integer mes,
            @PathVariable Integer ano) {

        return ResponseEntity.ok(
                desempenhoService.getDesempenhoUsuario(usuarioId, mes, ano)
        );
    }
}
