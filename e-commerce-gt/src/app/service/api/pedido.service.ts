import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido } from '../../entities/Pedido';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = 'http://localhost:8080/pedido';

  constructor(private http: HttpClient) { }

  // Obtener pedidos de un usuario
  obtenerPedidosUsuario(idUsuario: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/usuario/${idUsuario}`);
  }

  // Obtener todos los pedidos en curso
  obtenerPedidosEnCurso(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/en-curso`);
  }

  // Obtener un pedido por ID
  obtenerPedido(idPedido: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${idPedido}`);
  }

  // Actualizar fecha de entrega
  actualizarFechaEntrega(idPedido: number, nuevaFecha: string): Observable<void> {
    const body = { fecha_hora_entrega: nuevaFecha };
    return this.http.put<void>(`${this.apiUrl}/${idPedido}/fecha-entrega`, body);
  }

  // Marcar pedido como entregado
  marcarComoEntregado(idPedido: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idPedido}/entregar`, {});
  }

  // Actualizar estado del pedido
  actualizarEstado(idPedido: number, idEstado: number): Observable<void> {
    const body = { id_estado_pedido: idEstado };
    return this.http.put<void>(`${this.apiUrl}/${idPedido}/estado`, body);
  }
}