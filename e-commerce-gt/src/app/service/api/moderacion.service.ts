import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SancionResponse } from '../../entities/SancionResponse';
import { environment } from './article.service';

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
  private apiUrl = `${environment.apiUrl}/moderator`;

  constructor(private http: HttpClient) { }

  private crearParams(pagina: number, tamanoPagina: number): HttpParams {
    return new HttpParams()
      .set('pagina', pagina.toString())
      .set('tamanoPagina', tamanoPagina.toString());
  }

  obtenerSancionesPaginadas(pagina: number = 1, tamanoPagina: number = 10): Observable<SancionResponse> {
    return this.http.get<SancionResponse>(`${this.apiUrl}/sanciones`, { 
      params: this.crearParams(pagina, tamanoPagina) 
    });
  }

  obtenerArticulosPendientes(pagina: number = 1, tamanoPagina: number = 5): Observable<PaginacionResponse> {
    return this.http.get<PaginacionResponse>(`${this.apiUrl}/pendientes`, { 
      params: this.crearParams(pagina, tamanoPagina) 
    });
  }

  aprobarArticulo(idArticulo: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/aprobar/${idArticulo}`, {});
  }

  rechazarArticulo(idArticulo: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/rechazar/${idArticulo}`, {});
  }
}