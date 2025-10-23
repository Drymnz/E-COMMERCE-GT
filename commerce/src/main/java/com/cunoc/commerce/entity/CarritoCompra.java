package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarritoCompra {
    @JsonProperty("id_usuario")
    private int idUsuario;

    @JsonProperty("items")
    private List<ItemCarrito> items;

    @JsonProperty("id_vendedor")
    private int idVendedor; // Opcional, puede ser el admin u otro usuario

    // Constructores
    public CarritoCompra() {
        this.items = new ArrayList<>();
    }

    public CarritoCompra(int idUsuario, List<ItemCarrito> items) {
        this.idUsuario = idUsuario;
        this.items = items != null ? items : new ArrayList<>();
        this.idVendedor = 1; // Por defecto el admin
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public BigDecimal calcularTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Override
    public String toString() {
        return "CarritoCompra{" +
                "idUsuario=" + idUsuario +
                ", totalItems=" + getTotalItems() +
                ", total=" + calcularTotal() +
                '}';
    }
}