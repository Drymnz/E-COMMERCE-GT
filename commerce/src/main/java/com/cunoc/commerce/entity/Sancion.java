package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Sancion {
    @JsonProperty("id_sancion")
    private int idSancion;
    
    private String motivo;
    
    @JsonProperty("fecha_hora")
    private LocalDateTime fechaHora;
    
    @JsonProperty("id_usuario")
    private int idUsuario;
    
    // Información adicional del usuario (para mostrar en el frontend)
    @JsonProperty("nombre_usuario")
    private String nombreUsuario;
    
    @JsonProperty("email_usuario")
    private String emailUsuario;

    // Constructor completo
    public Sancion(int idSancion, String motivo, LocalDateTime fechaHora, int idUsuario) {
        this.idSancion = idSancion;
        this.motivo = motivo;
        this.fechaHora = fechaHora;
        this.idUsuario = idUsuario;
    }

    // Constructor para crear sanción
    public Sancion(String motivo, int idUsuario) {
        this.motivo = motivo;
        this.idUsuario = idUsuario;
        this.fechaHora = LocalDateTime.now();
    }

    // Constructor vacío
    public Sancion() {
    }

    // Getters
    public int getIdSancion() {
        return idSancion;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    // Setters
    public void setIdSancion(int idSancion) {
        this.idSancion = idSancion;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    @Override
    public String toString() {
        return "Sancion{" +
                "idSancion=" + idSancion +
                ", motivo='" + motivo + '\'' +
                ", fechaHora=" + fechaHora +
                ", idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", emailUsuario='" + emailUsuario + '\'' +
                '}';
    }
}