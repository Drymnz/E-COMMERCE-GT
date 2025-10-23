package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pago {
    @JsonProperty("id_pago")
    private int idPago;
    
    private double monto;

    // Constructores
    public Pago() {}

    public Pago(double monto) {
        this.monto = monto;
    }

    // Getters y Setters
    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}