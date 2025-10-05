package com.cunoc.commerce.config;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConexionDB {

    private static HikariDataSource dataSource;
    // PUERTO DE DOCKER 
    private static final String PORT = "8880";
    private static final String NAME_DOCKER = "commerce";
    private static final String NAME_URL = "localhost";
    private static final String URL = String.format("jdbc:postgresql://%s:%s/%s?sslmode=disable", NAME_URL, PORT, NAME_DOCKER);
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456";

    /**
     * Inicializar el pool de conexiones
     */
    public static void inicializar() {
        if (dataSource != null) {
            return;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");

            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(10000);

            dataSource = new HikariDataSource(config);
            System.out.println("Pool de conexiones inicializado correctamente");

        } catch (Exception e) {
            System.err.println("Error al inicializar el pool de conexiones: " + e.getMessage());
            e.printStackTrace();
            dataSource = null;
        }
    }

    /**
     * @return Connection objeto de conexión a la base de datos
     * @throws SQLException si hay error al obtener la conexión
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Error en la coneccion con PostgresSQL");
        }
        return dataSource.getConnection();
    }

    /**
     * @return true si está inicializado, false en caso contrario
     */
    public static boolean isInicializado() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Cerrar el pool de conexiones
     */
    public static void cerrar() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexiones cerrado");
        }
    }
}
