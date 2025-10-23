package com.cunoc.commerce.entity;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Article {
    @JsonProperty("id_articulo")
    private Integer idArticulo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagen;
    private Integer stock;

    @JsonProperty("id_estado_articulo")
    private Integer idEstadoArticulo;

    @JsonProperty("nombre_estado")
    private String nombreEstado;

    @JsonProperty("id_accion")
    private Integer idAccion;

    // NUEVO: Lista de IDs de categorías
    private List<String> categorias;

    public Article() {
    }

    public Article(String nombre, String descripcion, BigDecimal precio,
            String imagen, Integer stock, Integer idEstadoArticulo,Integer idAccion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.stock = stock;
        this.idEstadoArticulo = idEstadoArticulo;
        this.idAccion = idAccion;
    }

    // Getters y Setters existentes...
    public Integer getIdArticulo() {
        return idArticulo;
    }

    public Integer getIdAccion(){
        return idAccion;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setIdAccion(Integer idAccion){
        this.idAccion =idAccion; 
    }

    public Integer getIdEstadoArticulo() {
        return idEstadoArticulo;
    }

    public void setIdEstadoArticulo(Integer idEstadoArticulo) {
        this.idEstadoArticulo = idEstadoArticulo;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    // NUEVO: Getter y Setter para categorías
    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Article{" +
                "idArticulo=" + idArticulo +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", nombreEstado='" + nombreEstado + '\'' +
                ", categorias=" + categorias +
                '}';
    }
}