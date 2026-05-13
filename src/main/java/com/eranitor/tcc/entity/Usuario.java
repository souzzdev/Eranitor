package com.eranitor.tcc.entity;

import com.eranitor.tcc.enums.UsuarioRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table (name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idusuario")
    private Long idUsuario;

    @Column (name = "nome")
    private String nome;

    @Column (name = "login")
    private String login;

    @Column (name = "senhahash")
    private String password;

    @Column (name = "serie")
    private String serie;

    @Column (name = "instituicao")
    private String instituicao;

    @Column (name = "criadoem")
    private LocalDateTime criadoEm;

    private UsuarioRole role;

    public Usuario (String login, String password, UsuarioRole role) {
        this.login = login;
        this.password = password;
        this.role = role;

    }

    @OneToMany (mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Desempenho> desempenhos;

    @OneToMany (mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Materia> materias;

    @OneToMany (mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Agenda> agenda;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UsuarioRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
