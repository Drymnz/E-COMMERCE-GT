package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Pedido {
    @JsonProperty("id_pedido")
    private int idPedido;
    
    @JsonProperty("fecha_hora_entrega")
    private LocalDateTime fechaHoraEntrega;
    
    @JsonProperty("id_comprador")
    private int idComprador;
    
    @JsonProperty("id_estado_pedido")
    private int idEstadoPedido;

    // Constructores
    public Pedido() {
        this.fechaHoraEntrega = LocalDateTime.now().plusDays(10);
        this.idEstadoPedido = 1; // "En Curso"
    }

    public Pedido(int idComprador) {
        this.fechaHoraEntrega = LocalDateTime.now().plusDays(10);
        this.idComprador = idComprador;
        this.idEstadoPedido = 1; // "En Curso"
    }

    // Getters y Setters
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFechaHoraEntrega() {
        return fechaHoraEntrega;
    }

    public void setFechaHoraEntrega(LocalDateTime fechaHoraEntrega) {
        this.fechaHoraEntrega = fechaHoraEntrega;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public int getIdEstadoPedido() {
        return idEstadoPedido;
    }

    public void setIdEstadoPedido(int idEstadoPedido) {
        this.idEstadoPedido = idEstadoPedido;
    }
}