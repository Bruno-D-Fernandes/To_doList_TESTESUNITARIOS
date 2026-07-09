package edu.jUnitEMosquito.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "group_members")
public class UsuarioGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Roles roles;

    public enum Roles{
        OWNER,
        ADMIN,
        MEMBER
    };


    public UsuarioGrupo() {
    }

    public UsuarioGrupo(Group grupo, Usuario usuario, Roles roles) {
        this.grupo = grupo;
        this.usuario = usuario;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioGrupo that = (UsuarioGrupo) o;
        return Objects.equals(id, that.id) && Objects.equals(grupo, that.grupo) && Objects.equals(usuario, that.usuario) && roles == that.roles;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGrupo() {
        return grupo;
    }

    public void setGrupo(Group grupo) {
        this.grupo = grupo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }
}