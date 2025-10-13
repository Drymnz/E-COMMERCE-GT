package com.cunoc.commerce.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Publicacion {
    @JsonProperty("id_publicacion")
    private Integer idPublicacion;
    
    @JsonProperty("id_usuario")
    private Integer idUsuario;
    
    @JsonProperty("fecha_hora_entrega")
    private Timestamp fechaHoraEntrega;
    
    // El artículo completo para crear la publicación
    private Article articulo;
    
    // Constructor vacío
    public Publicacion() {}
    
    // Constructor para crear publicación con artículo
    public Publicacion(Integer idUsuario, Article articulo) {
        this.idUsuario = idUsuario;
        this.articulo = articulo;
    }
    
    // Constructor completo
    public Publicacion(Integer idPublicacion, Integer idUsuario, Article articulo, Timestamp fechaHoraEntrega) {
        this.idPublicacion = idPublicacion;
        this.idUsuario = idUsuario;
        this.articulo = articulo;
        this.fechaHoraEntrega = fechaHoraEntrega;
    }

    // Getters y Setters
    public Integer getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(Integer idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Timestamp getFechaHoraEntrega() {
        return fechaHoraEntrega;
    }

    public void setFechaHoraEntrega(Timestamp fechaHoraEntrega) {
        this.fechaHoraEntrega = fechaHoraEntrega;
    }

    public Article getArticulo() {
        return articulo;
    }

    public void setArticulo(Article articulo) {
        this.articulo = articulo;
    }

    @Override
    public String toString() {
        return "Publicacion{" +
                "idPublicacion=" + idPublicacion +
                ", idUsuario=" + idUsuario +
                ", fechaHoraEntrega=" + fechaHoraEntrega +
                ", articulo=" + articulo +
                '}';
    }
}