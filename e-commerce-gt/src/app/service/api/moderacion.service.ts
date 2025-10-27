import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SancionResponse } from '../../entities/SancionResponse';

export interface PaginacionResponse {
  articulos: any[];
  totalArticulos: number;
  totalPaginas: number;
  paginaActual: number;
}

@Injectable({
  providedIn: 'root'
})
export class ModeratorService {
  private apiUrl = 'http://localhost:8080/moderator';

  constructor(private http: HttpClient) { }


  // la clase ModeratorService
  obtenerSancionesPaginadas(pagina: number = 1, tamanoPagina: number = 10): Observable<SancionResponse> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('tamanoPagina', tamanoPagina.toString());

    return this.http.get<SancionResponse>(`${this.apiUrl}/sanciones`, { params });
  }

  //Obtiene artículos pendientes con paginación
  obtenerArticulosPendientes(pagina: number = 1, tamanoPagina: number = 5): Observable<PaginacionResponse> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('tamanoPagina', tamanoPagina.toString());

    return this.http.get<PaginacionResponse>(`${this.apiUrl}/pendientes`, { params });
  }

  //Aprueba un artículo
  aprobarArticulo(idArticulo: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/aprobar/${idArticulo}`, {});
  }

  //Rechaza un artículo
  rechazarArticulo(idArticulo: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/rechazar/${idArticulo}`, {});
  }
}

