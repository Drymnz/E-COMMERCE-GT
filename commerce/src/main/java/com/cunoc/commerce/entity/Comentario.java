package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comentario {
    @JsonProperty("id_comentario")
    private int idComentario;
    
    private String descripcion;
    private int puntuacion;
    
    @JsonProperty("id_usuario")
    private int idUsuario;
    
    @JsonProperty("id_articulo")
    private int idArticulo;
    
    public Comentario(int idComentario, String descripcion, int puntuacion, 
                      int idUsuario, int idArticulo) {
        this.idComentario = idComentario;
        this.descripcion = descripcion;
        this.puntuacion = puntuacion;
        this.idUsuario = idUsuario;
        this.idArticulo = idArticulo;
    }
    
    public Comentario(String descripcion, int puntuacion, int idUsuario, int idArticulo) {
        this.descripcion = descripcion;
        this.puntuacion = puntuacion;
        this.idUsuario = idUsuario;
        this.idArticulo = idArticulo;
    }
    
    public Comentario() {}
    
    public int getIdComentario() { return idComentario; }
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) {
        if (puntuacion >= 1 && puntuacion <= 5) {
            this.puntuacion = puntuacion;
        } else {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }
    }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public int getIdArticulo() { return idArticulo; }
    public void setIdArticulo(int idArticulo) { this.idArticulo = idArticulo; }
    
    @Override
    public String toString() {
        return "Comentario{" +
                "idComentario=" + idComentario +
                ", descripcion='" + descripcion + '\'' +
                ", puntuacion=" + puntuacion +
                ", idUsuario=" + idUsuario +
                ", idArticulo=" + idArticulo +
                '}';
    }
}