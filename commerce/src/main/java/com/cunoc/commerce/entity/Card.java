package com.cunoc.commerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Card {
    private String numero;
    private String cvv;
    
    @JsonProperty("fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    private BigDecimal saldo;
    
    @JsonProperty("id_usuario")
    private int idUsuario;

    // Constructor completo
    public Card(String numero, String cvv, LocalDate fechaVencimiento, 
                BigDecimal saldo, int idUsuario) {
        this.numero = numero;
        this.cvv = cvv;
        this.fechaVencimiento = fechaVencimiento;
        this.saldo = saldo;
        this.idUsuario = idUsuario;
    }

    // Constructor vacío
    public Card() {
        this.saldo = BigDecimal.ZERO;
    }

    // Getters
    public String getNumero() {
        return numero;
    }

    public String getCvv() {
        return cvv;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    // Setters
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Card{" +
                "numero='" + numero + '\'' +
                ", fechaVencimiento=" + fechaVencimiento +
                ", saldo=" + saldo +
                ", idUsuario=" + idUsuario +
                '}';
    }
}