package com.cunoc.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.cunoc.commerce.config.ConexionDB;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class CommerceApplication {

	public static void main(String[] args) {
		ConexionDB.inicializar();// iniciar la coneccion de la base de datos
		if (ConexionDB.isInicializado()) { // verifica si existe conecion
			SpringApplication.run(CommerceApplication.class, args);
		} else {
			System.out.println("La conexión falló al servidor");
		}
	}
}
