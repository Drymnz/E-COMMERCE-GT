import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';
import { Comentario } from '../../entities/Comentario';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'http://localhost:8080/commet';

  constructor(private http: HttpClient) { }

  //obtener todos los comentairos de un articulo
  getComentariosByArticulo(idArticulo: number): Observable<Comentario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/articulo/${idArticulo}`).pipe(
      map(comentarios => comentarios.map(c => Comentario.fromJSON(c))),
      catchError(error => {
        console.error('Error al obtener comentarios por artículo:', error);
        return throwError(() => error);
      })
    );
  }
  

  //enviar comentario 
  crearComentario(comentario: Comentario): Observable<Comentario> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const body = {
      descripcion: comentario.descripcion,
      puntuacion: comentario.puntuacion,
      id_usuario: comentario.id_usuario,
      id_articulo: comentario.id_articulo
    };

    return this.http.post<any>(this.apiUrl, body, { headers }).pipe(
      map(response => Comentario.fromJSON(response)),
      catchError(error => {
        console.error('Error al crear comentario:', error);
        return throwError(() => error);
      })
    );
  }

  //calcular promedio
  calcularPuntuacionPromedio(comentarios: Comentario[]): number {
    if (comentarios.length === 0) return 0;
    const suma = comentarios.reduce((acc, c) => acc + c.puntuacion, 0);
    return Math.round((suma / comentarios.length) * 10) / 10;
  }
}