package com.cunoc.commerce.service;

import com.cunoc.commerce.controller.datatoobject.NotificacionDAO;
import com.cunoc.commerce.controller.datatoobject.UsuarioDAO;
import com.cunoc.commerce.entity.EmailService;
import com.cunoc.commerce.entity.Notificacion;
import com.cunoc.commerce.entity.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificacionService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificacionDAO notificacionDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // cambio de estado en un pedido
    public boolean notificarCambioEstadoPedido(
            int idUsuario,
            int numeroPedido,
            String estadoAnterior,
            String estadoNuevo,
            String totalPedido,
            LocalDateTime fechaEstimadaEntrega) {

        try {
            Usuario usuario = usuarioDAO.findByEmailOrId(String.valueOf(idUsuario));
            if (usuario == null) {
                System.err.println("Usuario no encontrado: " + idUsuario);
                return false;
            }

            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                System.err.println("El usuario no tiene email configurado");
                guardarNotificacionSinEmail(idUsuario, numeroPedido, estadoAnterior, estadoNuevo);
                return false;
            }

            String nombreCompleto = obtenerNombreCompleto(usuario);

            boolean emailEnviado = emailService.notificarCambioEstadoPedido(
                    usuario.getEmail(),
                    nombreCompleto,
                    numeroPedido,
                    estadoAnterior,
                    estadoNuevo,
                    totalPedido,
                    fechaEstimadaEntrega);

            String mensaje = String.format(
                    "Tu pedido #%d cambio de estado: %s -> %s%s",
                    numeroPedido,
                    estadoAnterior,
                    estadoNuevo,
                    emailEnviado ? "" : " (Email no enviado)");

            guardarNotificacion(mensaje, idUsuario);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificacion de pedido: " + e.getMessage());
            e.printStackTrace();
            guardarNotificacionSinEmail(idUsuario, numeroPedido, estadoAnterior, estadoNuevo);
            return false;
        }
    }

    // Notifica aprobación
    public boolean notificarProductoAprobado(
            int idVendedor,
            String nombreProducto,
            String precioProducto,
            int idModerador) {

        try {
            Usuario vendedor = usuarioDAO.findByEmailOrId(String.valueOf(idVendedor));
            if (vendedor == null) {
                System.err.println("Vendedor no encontrado: " + idVendedor);
                return false;
            }

            if (vendedor.getEmail() == null || vendedor.getEmail().trim().isEmpty()) {
                System.err.println("El vendedor no tiene email configurado");
                String mensaje = String.format(
                        "Tu producto '%s' ha sido aprobado (Email no disponible)",
                        nombreProducto);
                guardarNotificacion(mensaje, idVendedor);
                return false;
            }

            Usuario moderador = usuarioDAO.findByEmailOrId(String.valueOf(idModerador));
            String nombreModerador = obtenerNombreCompleto(moderador, "Moderador");
            String nombreVendedor = obtenerNombreCompleto(vendedor);

            String asunto = "Producto Aprobado: " + nombreProducto;
            String mensajeEmail = construirMensajeAprobacion(
                    nombreVendedor,
                    nombreProducto,
                    precioProducto,
                    nombreModerador);

            boolean emailEnviado = emailService.enviarEmail(
                    vendedor.getEmail(),
                    asunto,
                    mensajeEmail);

            String mensaje = String.format(
                    "Tu producto '%s' ha sido aprobado y ya esta disponible en la tienda%s",
                    nombreProducto,
                    emailEnviado ? "" : " (Email no enviado)");

            guardarNotificacion(mensaje, idVendedor);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificacion de aprobacion: " + e.getMessage());
            e.printStackTrace();

            try {
                String mensaje = String.format(
                        "Tu producto '%s' ha sido aprobado (Email no enviado)",
                        nombreProducto);
                guardarNotificacion(mensaje, idVendedor);
            } catch (Exception ex) {
                System.err.println("Error al guardar notificacion: " + ex.getMessage());
            }

            return false;
        }
    }

    // Notifica rechazo de producto
    public boolean notificarProductoRechazado(
            int idVendedor,
            String nombreProducto,
            int idModerador,
            String motivoRechazo) {

        try {
            Usuario vendedor = usuarioDAO.findByEmailOrId(String.valueOf(idVendedor));
            if (vendedor == null) {
                System.err.println("Vendedor no encontrado: " + idVendedor);
                return false;
            }

            if (vendedor.getEmail() == null || vendedor.getEmail().trim().isEmpty()) {
                System.err.println("El vendedor no tiene email configurado");
                String mensaje = String.format(
                        "Tu producto '%s' ha sido rechazado (Email no disponible)",
                        nombreProducto);
                guardarNotificacion(mensaje, idVendedor);
                return false;
            }

            Usuario moderador = usuarioDAO.findByEmailOrId(String.valueOf(idModerador));
            String nombreModerador = obtenerNombreCompleto(moderador, "Moderador");
            String nombreVendedor = obtenerNombreCompleto(vendedor);

            String asunto = "Producto Rechazado: " + nombreProducto;
            String mensajeEmail = construirMensajeRechazo(
                    nombreVendedor,
                    nombreProducto,
                    nombreModerador,
                    motivoRechazo);

            boolean emailEnviado = emailService.enviarEmail(
                    vendedor.getEmail(),
                    asunto,
                    mensajeEmail);

            String mensaje = String.format(
                    "Tu producto '%s' ha sido rechazado. Motivo: %s%s",
                    nombreProducto,
                    motivoRechazo != null && !motivoRechazo.trim().isEmpty()
                            ? motivoRechazo
                            : "No especificado",
                    emailEnviado ? "" : " (Email no enviado)");

            guardarNotificacion(mensaje, idVendedor);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificacion de rechazo: " + e.getMessage());
            e.printStackTrace();

            try {
                String mensaje = String.format(
                        "Tu producto '%s' ha sido rechazado (Email no enviado)",
                        nombreProducto);
                guardarNotificacion(mensaje, idVendedor);
            } catch (Exception ex) {
                System.err.println("Error al guardar notificacion: " + ex.getMessage());
            }

            return false;
        }
    }

    // ============= mensajes =============

    private String obtenerNombreCompleto(Usuario usuario) {
        return obtenerNombreCompleto(usuario, "Usuario");
    }

    private String obtenerNombreCompleto(Usuario usuario, String nombrePorDefecto) {
        if (usuario == null) {
            return nombrePorDefecto;
        }

        String nombre = usuario.getNombre() != null ? usuario.getNombre().trim() : "";
        String apellido = usuario.getApellido() != null ? usuario.getApellido().trim() : "";

        if (nombre.isEmpty() && apellido.isEmpty()) {
            return nombrePorDefecto;
        }

        return (nombre + " " + apellido).trim();
    }

    private void guardarNotificacion(String mensaje, int idUsuario) {
        try {
            Notificacion notificacion = new Notificacion(mensaje, idUsuario);
            notificacionDAO.insert(notificacion);
        } catch (Exception e) {
            System.err.println("Error al guardar notificacion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardarNotificacionSinEmail(int idUsuario, int numeroPedido,
                                             String estadoAnterior, String estadoNuevo) {
        try {
            String mensaje = String.format(
                    "Tu pedido #%d cambio de estado: %s -> %s (Email no enviado)",
                    numeroPedido, estadoAnterior, estadoNuevo);
            guardarNotificacion(mensaje, idUsuario);
        } catch (Exception ex) {
            System.err.println("Error al guardar notificacion: " + ex.getMessage());
        }
    }

    private String construirMensajeAprobacion(String nombreVendedor, String nombreProducto,
                                              String precioProducto, String nombreModerador) {
        return "Hola " + nombreVendedor + ",\n\n" +
                "Tu producto ha sido aprobado.\n\n" +
                "Producto: " + nombreProducto + "\n" +
                "Precio: Q" + precioProducto + "\n" +
                "Aprobado por: " + nombreModerador + "\n" +
                "Fecha: " + LocalDateTime.now().format(dateFormatter) + "\n\n" +
                "Tu producto ya esta visible en la tienda y los usuarios pueden comprarlo.\n\n" +
                "Nota: Se aplica una comision del 5% por cada venta.\n\n" +
                "Tu Tienda";
    }

    private String construirMensajeRechazo(String nombreVendedor, String nombreProducto,
                                           String nombreModerador, String motivoRechazo) {
        return "Hola " + nombreVendedor + ",\n\n" +
                "Tu producto ha sido rechazado.\n\n" +
                "Producto: " + nombreProducto + "\n" +
                "Revisado por: " + nombreModerador + "\n" +
                "Fecha: " + LocalDateTime.now().format(dateFormatter) + "\n\n" +
                (motivoRechazo != null && !motivoRechazo.trim().isEmpty()
                        ? "Motivo del rechazo:\n" + motivoRechazo.trim() + "\n\n"
                        : "") +
                "Por favor revisa los detalles del producto y vuelve a publicarlo corrigiendo los problemas indicados.\n\n" +
                "Tu Tienda";
    }
}