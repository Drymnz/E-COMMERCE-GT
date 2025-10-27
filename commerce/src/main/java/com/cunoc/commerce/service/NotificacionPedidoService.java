package com.cunoc.commerce.service;

import com.cunoc.commerce.controller.datatoobject.ConstantDAO;
import com.cunoc.commerce.controller.datatoobject.NotificacionDAO;
import com.cunoc.commerce.controller.datatoobject.UsuarioDAO;
import com.cunoc.commerce.entity.EmailService;
import com.cunoc.commerce.entity.Notificacion;
import com.cunoc.commerce.entity.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificacionPedidoService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private NotificacionDAO notificacionDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    //fecha de entrega de su pedido ha cambiado
    public boolean notificarCambioFechaEntrega(int idPedido, LocalDateTime nuevaFecha) {
        try {
            Usuario usuario = usuarioDAO.findByPedidoId(idPedido);
            if (usuario == null) {
                System.err.println("Usuario no encontrado para pedido: " + idPedido);
                return false;
            }

            String asunto = "Cambio en Fecha de Entrega - Pedido #" + idPedido;
            String mensaje = construirMensajeCambioFecha(usuario, idPedido, nuevaFecha);

            boolean emailEnviado = emailService.enviarEmail(usuario.getEmail(), asunto, mensaje);

            String notifMensaje = String.format(
                    "La fecha de entrega de tu pedido #%d ha sido actualizada para el %s%s",
                    idPedido,
                    nuevaFecha.format(dateFormatter),
                    emailEnviado ? "" : " (Email no enviado)");

            Notificacion notificacion = new Notificacion(notifMensaje, usuario.getIdUsuario());
            notificacionDAO.insert(notificacion);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificación de cambio de fecha: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   //estado de su pedido ha cambiado
    public boolean notificarCambioEstado(int idPedido, int idEstado) {
        try {
            Usuario usuario = usuarioDAO.findByPedidoId(idPedido);
            if (usuario == null) {
                System.err.println("Usuario no encontrado para pedido: " + idPedido);
                return false;
            }

            ConstantDAO constantDAO = new ConstantDAO();
            List<String> estados = constantDAO.getEstadosPedido();
            String nombreEstado = (idEstado > 0 && idEstado <= estados.size()) 
                    ? estados.get(idEstado - 1) 
                    : "Desconocido";

            String asunto = "Actualización de Pedido #" + idPedido;
            String mensaje = construirMensajeCambioEstado(usuario, idPedido, nombreEstado);

            boolean emailEnviado = emailService.enviarEmail(usuario.getEmail(), asunto, mensaje);

            String notifMensaje = String.format(
                    "Tu pedido #%d cambió a estado: %s%s",
                    idPedido,
                    nombreEstado,
                    emailEnviado ? "" : " (Email no enviado)");

            Notificacion notificacion = new Notificacion(notifMensaje, usuario.getIdUsuario());
            notificacionDAO.insert(notificacion);

            return emailEnviado;

        } catch (Exception e) {
            System.err.println("Error al enviar notificación de cambio de estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ============= mensajes  =============

    private String construirMensajeCambioFecha(Usuario usuario, int idPedido, LocalDateTime nuevaFecha) {
        return "Hola " + usuario.getNombre() + " " + usuario.getApellido() + ",\n\n" +
                "Te informamos que la fecha de entrega de tu pedido ha sido actualizada.\n\n" +
                "Pedido: #" + idPedido + "\n" +
                "Nueva fecha de entrega: " + nuevaFecha.format(dateFormatter) + "\n\n" +
                "Cualquier duda, estamos a tu disposición.\n\n" +
                "Tu Tienda";
    }

    private String construirMensajeCambioEstado(Usuario usuario, int idPedido, String nombreEstado) {
        return "Hola " + usuario.getNombre() + " " + usuario.getApellido() + ",\n\n" +
                "Tu pedido ha sido actualizado.\n\n" +
                "Pedido: #" + idPedido + "\n" +
                "Nuevo estado: " + nombreEstado + "\n" +
                "Fecha de actualización: " + LocalDateTime.now().format(dateFormatter) + "\n\n" +
                "Gracias por tu compra.\n\n" +
                "Tu Tienda";
    }
}