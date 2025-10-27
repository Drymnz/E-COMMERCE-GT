package com.cunoc.commerce.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@tutienda.com}")
    private String emailFrom;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Notifica cambio de estado en un pedido
     */
    public boolean notificarCambioEstadoPedido(
            String emailUsuario,
            String nombreUsuario,
            int numeroPedido,
            String estadoAnterior,
            String estadoNuevo,
            String totalPedido,
            LocalDateTime fechaEstimadaEntrega) {
        
        if (!esEmailValido(emailUsuario)) {
            System.err.println("Email inválido: " + emailUsuario);
            return false;
        }
        
        String asunto = "Actualización de Pedido #" + numeroPedido;
        
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola ").append(nombreUsuario).append(",\n\n");
        mensaje.append("Tu pedido ha cambiado de estado:\n\n");
        mensaje.append("Número de Pedido: #").append(numeroPedido).append("\n");
        mensaje.append("Estado Anterior: ").append(estadoAnterior).append("\n");
        mensaje.append("Estado Actual: ").append(estadoNuevo).append("\n");
        mensaje.append("Total: Q").append(totalPedido).append("\n");
        
        if (fechaEstimadaEntrega != null) {
            mensaje.append("Entrega Estimada: ").append(fechaEstimadaEntrega.format(dateFormatter)).append("\n");
        }
        
        mensaje.append("\n").append(obtenerMensajeEstado(estadoNuevo)).append("\n\n");
        mensaje.append("Gracias por tu compra.\n");
        mensaje.append("Tu Tienda");
        
        return enviarEmail(emailUsuario, asunto, mensaje.toString());
    }
    
    /**
     * Envía un email genérico con manejo de errores
     */
    public boolean enviarEmail(String destinatario, String asunto, String mensaje) {
        if (!esEmailValido(destinatario)) {
            System.err.println("Email de destinatario inválido: " + destinatario);
            return false;
        }
        
        if (asunto == null || asunto.trim().isEmpty()) {
            System.err.println("El asunto del email no puede estar vacío");
            return false;
        }
        
        if (mensaje == null || mensaje.trim().isEmpty()) {
            System.err.println("El mensaje del email no puede estar vacío");
            return false;
        }
        
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(destinatario);
            mailMessage.setSubject(asunto);
            mailMessage.setText(mensaje);
            
            mailSender.send(mailMessage);
            System.out.println("Email enviado exitosamente a: " + destinatario);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al enviar email a " + destinatario + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Valida formato de email
     */
    private boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Obtiene mensaje personalizado según el estado del pedido
     */
    private String obtenerMensajeEstado(String estado) {
        if (estado == null) {
            return "El estado de tu pedido ha sido actualizado.";
        }
        
        return switch (estado.toLowerCase()) {
            case "pendiente" -> "Tu pedido está siendo procesado.";
            case "en_curso", "en curso" -> "Tu pedido está en camino.";
            case "entregado" -> "Tu pedido ha sido entregado. ¡Gracias por tu compra!";
            case "cancelado" -> "Tu pedido ha sido cancelado.";
            default -> "El estado de tu pedido ha sido actualizado.";
        };
    }
}