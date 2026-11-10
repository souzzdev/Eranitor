package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AgendaCreateDTO;
import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.entity.Agenda;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.AgendaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AgendaDTO criarAgenda(AgendaCreateDTO dto, Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        if (dto == null || dto.titulo() == null || dto.titulo().isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Agenda agenda = new Agenda();
        agenda.setUsuario(usuario);
        agenda.setData(dto.data());
        agenda.setTitulo(dto.titulo());
        agenda.setTipo(dto.tipo());

        Agenda salvo = agendaRepository.save(agenda);

        return new AgendaDTO(
                salvo.getId(),
                salvo.getUsuario().getIdUsuario(),
                salvo.getData(),
                salvo.getTitulo(),
                salvo.getTipo()
        );
    }

    public List<AgendaDTO> listarPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        List<Agenda> agendas = agendaRepository.findByUsuario_IdUsuario(usuarioId);

        return agendas.stream()
                .map(a -> new AgendaDTO(
                        a.getId(),
                        a.getUsuario().getIdUsuario(),
                        a.getData(),
                        a.getTitulo(),
                        a.getTipo()
                ))
                .toList();
    }

    public AgendaDTO atualizarAgenda(Integer id, AgendaCreateDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da agenda é obrigatório");
        }

        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda não encontrada"));

        agenda.setData(dto.data());
        agenda.setTitulo(dto.titulo());
        agenda.setTipo(dto.tipo());

        Agenda salvo = agendaRepository.save(agenda);

        return new AgendaDTO(
                salvo.getId(),
                salvo.getUsuario().getIdUsuario(),
                salvo.getData(),
                salvo.getTitulo(),
                salvo.getTipo()
        );
    }

    public void deletarAgenda(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da agenda é obrigatório");
        }

        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda não encontrada"));

        agendaRepository.delete(agenda);
    }
}
