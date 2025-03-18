package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"Usuarios\"")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "convenio_id")
    private Integer convenioId;

    @Column(name = "id_externa", length = 100)
    private String idExterna;



    @Column(name = "password", length = 50)
    private String password;

    @Column(name = "email", length = 50)
    private String email;

    // Getters and setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }


    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getConvenioId() {
        return convenioId;
    }

    public void setConvenioId(Integer convenioId) {
        this.convenioId = convenioId;
    }

    public String getIdExterna() {
        return idExterna;
    }

    public void setIdExterna(String idExterna) {
        this.idExterna = idExterna;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
