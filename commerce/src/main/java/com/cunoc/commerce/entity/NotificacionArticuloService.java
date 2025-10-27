package com.cunoc.commerce.entity;

import com.cunoc.commerce.controller.datatoobject.ArticleDAO;
import com.cunoc.commerce.controller.datatoobject.NotificacionDAO;
import com.cunoc.commerce.controller.datatoobject.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificacionArticuloService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ArticleDAO articuloDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private NotificacionDAO notificacionDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    //artículo fue aprobado
    public boolean notificarAprobacion(int idArticulo, int idModerador) {
        try {
            Article articulo = articuloDAO.findById(idArticulo);
            if (articulo == null) {
                System.err.println("Artículo no encontrado: " + idArticulo);
                return false;
            }

            // Obtener vendedor desde la publicación del artículo
            Usuario vendedor = usuarioDAO.findByArticuloId(idArticulo);
            if (vendedor == null) {
                System.err.println("Vendedor no encontrado para artículo: " + idArticulo);
                return false;
            }

            Usuario moderador = usuarioDAO.findByEmailOrId(String.valueOf(idModerador));
            String nombreModerador = moderador != null
                    ? moderador.getNombre() + " " + moderador.getApellido()
                    : "Moderador";

            String asunto = "Producto Aprobado: " + articulo.getNombre();
            String mensaje = construirMensajeAprobacion(vendedor, articulo, nombreModerador);

            boolean emailEnviado = emailService.enviarEmail(vendedor.getEmail(), asunto, mensaje);

            String notifMensaje = String.format(
                    "Tu producto '%s' ha sido aprobado y ya está disponible en la tienda%s",
                    articulo.getNombre(),
                    emailEnviado ? "" : " (Email no enviado)");

            Notificacion notificacion = new Notificacion(notifMensaje, vendedor.getIdUsuario());
            notificacionDAO.insert(notificacion);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificación de aprobación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //artículo fue rechazado
    public boolean notificarRechazo(int idArticulo, int idModerador, String motivoRechazo) {
        try {
            Article articulo = articuloDAO.findById(idArticulo);
            if (articulo == null) {
                System.err.println("Artículo no encontrado: " + idArticulo);
                return false;
            }

            // Obtener vendedor desde la publicación del artículo
            Usuario vendedor = usuarioDAO.findByArticuloId(idArticulo);
            if (vendedor == null) {
                System.err.println("Vendedor no encontrado para artículo: " + idArticulo);
                return false;
            }

            Usuario moderador = usuarioDAO.findByEmailOrId(String.valueOf(idModerador));
            String nombreModerador = moderador != null
                    ? moderador.getNombre() + " " + moderador.getApellido()
                    : "Moderador";

            String asunto = "Producto Rechazado: " + articulo.getNombre();
            String mensaje = construirMensajeRechazo(vendedor, articulo, nombreModerador, motivoRechazo);

            boolean emailEnviado = emailService.enviarEmail(vendedor.getEmail(), asunto, mensaje);

            String notifMensaje = String.format(
                    "Tu producto '%s' ha sido rechazado. Motivo: %s%s",
                    articulo.getNombre(),
                    motivoRechazo != null ? motivoRechazo : "No especificado",
                    emailEnviado ? "" : " (Email no enviado)");

            Notificacion notificacion = new Notificacion(notifMensaje, vendedor.getIdUsuario());
            notificacionDAO.insert(notificacion);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificación de rechazo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ============= CONSTRUIR MENSAJES =============

    private String construirMensajeAprobacion(Usuario vendedor, Article articulo, String nombreModerador) {
        return "Hola " + vendedor.getNombre() + " " + vendedor.getApellido() + ",\n\n" +
                "Tu producto ha sido aprobado.\n\n" +
                "Producto: " + articulo.getNombre() + "\n" +
                "Precio: Q" + articulo.getPrecio() + "\n" +
                "Aprobado por: " + nombreModerador + "\n" +
                "Fecha: " + LocalDateTime.now().format(dateFormatter) + "\n\n" +
                "Tu producto ya está visible en la tienda y los usuarios pueden comprarlo.\n\n" +
                "Nota: Se aplica una comisión del 5% por cada venta.\n\n" +
                "Tu Tienda";
    }

    private String construirMensajeRechazo(Usuario vendedor, Article articulo,
            String nombreModerador, String motivoRechazo) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola ").append(vendedor.getNombre()).append(" ").append(vendedor.getApellido()).append(",\n\n");
        mensaje.append("Tu producto ha sido rechazado.\n\n");
        mensaje.append("Producto: ").append(articulo.getNombre()).append("\n");
        mensaje.append("Revisado por: ").append(nombreModerador).append("\n");
        mensaje.append("Fecha: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");

        if (motivoRechazo != null && !motivoRechazo.isEmpty()) {
            mensaje.append("Motivo del rechazo:\n");
            mensaje.append(motivoRechazo).append("\n\n");
        }

        mensaje.append(
                "Por favor revisa los detalles del producto y vuelve a publicarlo corrigiendo los problemas indicados.\n\n");
        return mensaje.toString();
    }
}