import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Notificacion } from '../../entities/Notificacion';
import { environment } from './article.service';

export interface NotificacionesPaginadas {
  notificaciones: Notificacion[];
  currentPage: number;
  pageSize: number;
  totalNotificaciones: number;
  totalPages: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private apiUrl = `${environment.apiUrl}/notificaciones`;

  constructor(private http: HttpClient) { }

  //Obtener todas las notificaciones del sistema paginadas
  obtenerNotificacionesPaginadas(
    page: number = 1, 
    pageSize: number = 10
  ): Observable<NotificacionesPaginadas> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map(response => ({
        notificaciones: response.notificaciones.map((n: any) => Notificacion.fromJSON(n)),
        currentPage: response.currentPage,
        pageSize: response.pageSize,
        totalNotificaciones: response.totalNotificaciones,
        totalPages: response.totalPages
      }))
    );
  }
  
}