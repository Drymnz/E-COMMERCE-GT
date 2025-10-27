import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from './article.service';

export interface VentaTotal {
  id_articulo: number;
  nombre_articulo: string;
  cantidad_vendida: number;
  total_ventas: number;
}

export interface TotalGeneral {
  total_general: number;
}

@Injectable({
  providedIn: 'root'
})
export class VentaService {
  private apiUrl = `${environment.apiUrl}/ventas`;

  constructor(private http: HttpClient) { }

 //Obtiene el listado de ventas por artículo
  obtenerTotalVentas(): Observable<VentaTotal[]> {
    return this.http.get<VentaTotal[]>(`${this.apiUrl}/total`);
  }

  //Obtiene el total general de todas las ventas
  obtenerTotalGeneral(): Observable<number> {
    return this.http.get<TotalGeneral>(`${this.apiUrl}/total-general`).pipe(
      map(response => response.total_general)
    );
  }
}