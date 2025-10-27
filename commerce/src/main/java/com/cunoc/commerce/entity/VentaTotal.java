package com.cunoc.commerce.entity;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VentaTotal {
    
    @JsonProperty("id_articulo")
    private Integer idArticulo;
    
    @JsonProperty("nombre_articulo")
    private String nombreArticulo;
    
    @JsonProperty("cantidad_vendida")
    private Integer cantidadVendida;
    
    @JsonProperty("total_ventas")
    private BigDecimal totalVentas;

    public VentaTotal() {
    }

    public VentaTotal(Integer idArticulo, String nombreArticulo, 
                      Integer cantidadVendida, BigDecimal totalVentas) {
        this.idArticulo = idArticulo;
        this.nombreArticulo = nombreArticulo;
        this.cantidadVendida = cantidadVendida;
        this.totalVentas = totalVentas;
    }

    // Getters y Setters
    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    @Override
    public String toString() {
        return "VentaTotal{" +
                "idArticulo=" + idArticulo +
                ", nombreArticulo='" + nombreArticulo + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                ", totalVentas=" + totalVentas +
                '}';
    }
}