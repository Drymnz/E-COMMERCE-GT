package com.cunoc.commerce.entity;

import java.math.BigDecimal;

public class Article {
    private Integer idArticulo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagen; // Base64
    private Integer stock;
    private Integer idEstadoArticulo;
    private String nombreEstado; 
    
    public Article() {}
    
    public Article(String nombre, String descripcion, BigDecimal precio, 
                   String imagen, Integer stock, Integer idEstadoArticulo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.stock = stock;
        this.idEstadoArticulo = idEstadoArticulo;
    }

    // Getters y Setters
    public Integer getIdArticulo() {
        return idArticulo;
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

    @Override
    public String toString() {
        return "Article{" +
                "idArticulo=" + idArticulo +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", nombreEstado='" + nombreEstado + '\'' +
                '}';
    }
}