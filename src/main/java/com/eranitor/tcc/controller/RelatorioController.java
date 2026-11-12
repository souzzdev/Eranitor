package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.RelatorioDTO;
import com.eranitor.tcc.services.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @PostMapping("/periodo/{usuarioId}")
    public ResponseEntity<RelatorioDTO> gerarPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {

        LocalDate inicio = LocalDate.parse(dataInicio);
        LocalDate fim = LocalDate.parse(dataFim);

        return ResponseEntity.ok(
                relatorioService.gerarRelatorioPeriodo(usuarioId, inicio, fim)
        );
    }

    @PostMapping("/materia/{materiaId}")
    public ResponseEntity<RelatorioDTO> gerarMateria(
            @PathVariable Long materiaId,
            @RequestParam String mesInicio,
            @RequestParam String mesFim) {

        LocalDate inicio = LocalDate.parse(mesInicio);
        LocalDate fim = LocalDate.parse(mesFim);

        return ResponseEntity.ok(
                relatorioService.gerarRelatorioMateria(materiaId, inicio, fim)
        );
    }

    @GetMapping("/usuario/{usuarioId}/tempo/{mes}/{ano}")
    public ResponseEntity<RelatorioDTO> gerarTempoEstudo(
            @PathVariable Long usuarioId,
            @PathVariable Integer mes,
            @PathVariable Integer ano) {

        return ResponseEntity.ok(
                relatorioService.gerarRelatorioTempoEstudo(usuarioId, mes, ano)
        );
    }
}
