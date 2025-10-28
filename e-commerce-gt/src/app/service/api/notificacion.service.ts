import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
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

  obtenerNotificacionesPaginadas(page: number = 1, pageSize: number = 10): Observable<NotificacionesPaginadas> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map(r => ({
        notificaciones: r.notificaciones.map((n: any) => Notificacion.fromJSON(n)),
        currentPage: r.currentPage,
        pageSize: r.pageSize,
        totalNotificaciones: r.totalNotificaciones,
        totalPages: r.totalPages
      }))
    );
  }
}