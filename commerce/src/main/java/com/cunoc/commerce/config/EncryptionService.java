package com.cunoc.commerce.config;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class EncryptionService {
     private static final int COST = 12; // Costo de encriptación BCrypt
    
    /**
     * @param password Contraseña en texto plano
     * @return Contraseña encriptada
     */
    public String encrypt(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }
    
    /**
     * @param password Contraseña en texto plano
     * @param hashedPassword Hash de la contraseña
     * @return true si coinciden, false en caso contrario
     */
    public boolean verify(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
        } catch (Exception e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
}
