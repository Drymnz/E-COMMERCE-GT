import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './article.service';

export interface RespuestaCompra {
  exitoso: boolean;
  mensaje: string;
  id_compra?: number;
  id_pedido?: number;
  id_pago?: number;
  total?: number;
}

@Injectable({
  providedIn: 'root'
})
export class CompraService {
  // URL directa sin environment
  private apiUrl = `${environment.apiUrl}/compra`;

  constructor(private http: HttpClient) { }

  //Procesa una compra completa desde el carrito
  procesarCompra(carrito: any): Observable<RespuestaCompra> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<RespuestaCompra>(
      `${this.apiUrl}/procesar`,
      carrito,
      { headers }
    );
  }

  //Test del servicio verificar
  test(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' });
  }
}