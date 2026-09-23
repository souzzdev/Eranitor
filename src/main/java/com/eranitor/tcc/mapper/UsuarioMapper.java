package com.eranitor.tcc.mapper;

import com.eranitor.tcc.dto.AtualizarPerfilDTO;
import com.eranitor.tcc.dto.RegisterDTO;
import com.eranitor.tcc.dto.UsuarioResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UsuarioMapper {

    private final ModelMapper modelMapper;

    public UsuarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Usuario toEntity(RegisterDTO dto) {
        Objects.requireNonNull(dto, "O DTO de cadastro não pode ser nulo.");

        Usuario usuario = modelMapper.map(dto, Usuario.class);

        usuario.setPassword(null);

        return usuario;
    }

    public Usuario updateEntity(AtualizarPerfilDTO dto, Usuario usuario) {
        Objects.requireNonNull(dto, "O DTO de atualização não pode ser nulo.");
        Objects.requireNonNull(usuario, "O usuário não pode ser nulo.");

        modelMapper.map(dto, usuario);

        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        Objects.requireNonNull(usuario, "O usuário não pode ser nulo.");

        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }
}
