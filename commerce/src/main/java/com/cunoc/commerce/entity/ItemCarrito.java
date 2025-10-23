package com.cunoc.commerce.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemCarrito {
    @JsonProperty("articulo")
    private Article articulo;

    @JsonProperty("cantidad")
    private int cantidad;

    // Constructores
    public ItemCarrito() {
    }

    public ItemCarrito(Article articulo, int cantidad) {
        this.articulo = articulo;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Article getArticulo() {
        return articulo;
    }

    public void setArticulo(Article articulo) {
        this.articulo = articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return articulo != null && articulo.getPrecio() != null
                ? articulo.getPrecio().multiply(BigDecimal.valueOf(cantidad))
                : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ItemCarrito{" +
                "articulo=" + articulo +
                ", cantidad=" + cantidad +
                '}';
    }
}