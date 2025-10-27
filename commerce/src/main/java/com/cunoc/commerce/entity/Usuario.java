package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {
    @JsonProperty("id_usuario")
    private int idUsuario;
    
    private String nombre;
    private String apellido;
    private String email;
    
    @JsonProperty("id_estado")
    private int idEstado;  
    
    @JsonProperty("id_rol")
    private int idRol; 
    
    private String password; 

    // Constructor completo
    public Usuario(int idUsuario, String nombre, String apellido, String email, int idEstado, int idRol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.idEstado = idEstado;
        this.idRol = idRol;
    }

    // Constructor para creación de usuario sin ID
    public Usuario(String nombre, String apellido, String email, String password, int idRol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.idRol = idRol;
        this.idEstado = 1; 
    }

    // Constructor vacío
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

    public int getIdEstado() { 
        return idEstado;
    }

    public int getIdRol() {  
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

    public void setIdEstado(int idEstado) { 
        this.idEstado = idEstado;
    }

    public void setIdRol(int idRol) { 
        this.idRol = idRol;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //  estático
    public static Usuario crearDesdeDatos(int id, String nombre, String apellido, String email, int idEstado, int idRol) {
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
                ", idEstado=" + idEstado +
                ", idRol=" + idRol +
                '}';
    }
}