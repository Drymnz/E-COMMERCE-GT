import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido } from '../../entities/Pedido';
import { environment } from './article.service';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = `${environment.apiUrl}/pedido`;

  constructor(private http: HttpClient) { }

  obtenerPedidosUsuario(idUsuario: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/usuario/${idUsuario}`);
  }

  obtenerPedidosEnCurso(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/en-curso`);
  }

  obtenerPedido(idPedido: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${idPedido}`);
  }

  actualizarFechaEntrega(idPedido: number, nuevaFecha: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idPedido}/fecha-entrega`, { fecha_hora_entrega: nuevaFecha });
  }

  marcarComoEntregado(idPedido: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idPedido}/entregar`, {});
  }

  actualizarEstado(idPedido: number, idEstado: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idPedido}/estado`, { id_estado_pedido: idEstado });
  }
}