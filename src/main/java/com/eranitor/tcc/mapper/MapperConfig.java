package com.eranitor.tcc.mapper;

import com.eranitor.tcc.dto.UsuarioResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(
                        org.modelmapper.config.Configuration.AccessLevel.PRIVATE
                );

        modelMapper.createTypeMap(Usuario.class, UsuarioResponseDTO.class)
                .setConverter(context -> {
                    Usuario usuario = context.getSource();

                    return new UsuarioResponseDTO(
                            usuario.getIdUsuario(),
                            usuario.getNome(),
                            usuario.getEmail(),
                            usuario.getSerie(),
                            usuario.getInstituicao(),
                            usuario.getCriadoEm(),
                            usuario.getRole()
                    );
                });

        return modelMapper;
    }
}
