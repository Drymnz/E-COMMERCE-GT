package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Notificacion {
    @JsonProperty("id_notificacion")
    private Integer idNotificacion;
    
    private String mensaje;
    
    @JsonProperty("fecha_hora")
    private LocalDateTime fechaHora;
    
    @JsonProperty("id_usuario")
    private Integer idUsuario;

    // Constructor completo
    public Notificacion(Integer idNotificacion, String mensaje, LocalDateTime fechaHora, Integer idUsuario) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.idUsuario = idUsuario;
    }

    // Constructor para crear notificaciÃ³n sin ID (para insertar)
    public Notificacion(String mensaje, Integer idUsuario) {
        this.mensaje = mensaje;
        this.fechaHora = LocalDateTime.now();
        this.idUsuario = idUsuario;
    }

    // Constructor vacÃ­o
    public Notificacion() {
    }

    // Getters
    public Integer getIdNotificacion() {
        return idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    // Setters
    public void setIdNotificacion(Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", mensaje='" + mensaje + '\'' +
                ", fechaHora=" + fechaHora +
                ", idUsuario=" + idUsuario +
                '}';
    }
}