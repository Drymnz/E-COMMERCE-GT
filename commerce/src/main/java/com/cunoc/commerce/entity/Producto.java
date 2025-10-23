package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Producto {
    @JsonProperty("id_producto")
    private int idProducto;
    
    @JsonProperty("id_compra")
    private int idCompra;
    
    @JsonProperty("id_articulo")
    private int idArticulo;
    
    private int cantidad;

    // Constructores
    public Producto() {}

    public Producto(int idCompra, int idArticulo, int cantidad) {
        this.idCompra = idCompra;
        this.idArticulo = idArticulo;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}