package com.cunoc.commerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {
    
    @GetMapping
    public String obtenerArticulos() {
        return "Lista de artículos";
    }
}