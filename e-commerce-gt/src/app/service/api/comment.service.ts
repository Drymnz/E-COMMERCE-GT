import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';
import { Comentario } from '../../entities/Comentario';
import { environment } from './article.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = `${environment.apiUrl}/commet`;

  constructor(private http: HttpClient) { }

  getComentariosByArticulo(idArticulo: number): Observable<Comentario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/articulo/${idArticulo}`).pipe(
      map(comentarios => comentarios.map(c => Comentario.fromJSON(c))),
      catchError(e => throwError(() => e))
    );
  }

  crearComentario(c: Comentario): Observable<Comentario> {
    return this.http.post<any>(this.apiUrl, {
      descripcion: c.descripcion,
      puntuacion: c.puntuacion,
      id_usuario: c.id_usuario,
      id_articulo: c.id_articulo
    }).pipe(
      map(res => Comentario.fromJSON(res)),
      catchError(e => throwError(() => e))
    );
  }

  calcularPuntuacionPromedio(comentarios: Comentario[]): number {
    if (!comentarios.length) return 0;
    return Math.round(comentarios.reduce((acc, c) => acc + c.puntuacion, 0) / comentarios.length * 10) / 10;
  }
}