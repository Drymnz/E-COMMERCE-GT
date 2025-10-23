package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Compra {
    @JsonProperty("id_compra")
    private int idCompra;
    
    @JsonProperty("fecha_hora")
    private LocalDateTime fechaHora;
    
    @JsonProperty("id_comprador")
    private int idComprador;
    
    @JsonProperty("id_vendedor")
    private int idVendedor;
    
    @JsonProperty("id_pago")
    private int idPago;

    // Constructores
    public Compra() {
        this.fechaHora = LocalDateTime.now();
    }

    public Compra(int idComprador, int idVendedor, int idPago) {
        this.fechaHora = LocalDateTime.now();
        this.idComprador = idComprador;
        this.idVendedor = idVendedor;
        this.idPago = idPago;
    }

    // Getters y Setters
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }
}