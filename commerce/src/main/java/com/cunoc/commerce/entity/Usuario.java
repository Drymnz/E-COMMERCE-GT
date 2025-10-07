package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {
    @JsonProperty("id_usuario")
    private int idUsuario;
    
    private String nombre;
    private String apellido;
    private String email;
    
    @JsonProperty("id_estado")
    private String idEstado;
    
    @JsonProperty("id_rol")
    private String idRol;
    
    private String password; 

    // Constructor completo
    public Usuario(int idUsuario, String nombre, String apellido, String email, String idEstado, String idRol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.idEstado = idEstado;
        this.idRol = idRol;
    }

    // Constructor para creación de usuario sin ID
    public Usuario(String nombre, String apellido, String email, String password, String idRol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.idRol = idRol;
        this.idEstado = "activo";
    }

    // Constructor vacío para frameworks
    public Usuario() {
    }

    // Getters
    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public String getIdRol() {
        return idRol;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Método estático para crear un usuario sin contraseña
    public static Usuario crearDesdeDatos(int id, String nombre, String apellido, String email, String idEstado, String idRol) {
        return new Usuario(id, nombre, apellido, email, idEstado, idRol);
    }

    // Actualiza los datos básicos del usuario
    public void actualizarDatos(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", idEstado='" + idEstado + '\'' +
                ", idRol='" + idRol + '\'' +
                '}';
    }
}