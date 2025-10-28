import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Sancion } from '../../entities/Sancion';
import { environment } from './article.service';

@Injectable({
  providedIn: 'root'
})
export class SancionService {
  private apiUrl = `${environment.apiUrl}/sancion`;

  constructor(private http: HttpClient) { }

  crearSancion(motivo: string, id_usuario: number): Observable<Sancion> {
    return this.http.post<any>(this.apiUrl, {
      motivo,
      id_usuario,
      fecha_hora: new Date().toISOString()
    }).pipe(map(r => Sancion.fromJSON(r)));
  }

  obtenerTodasSanciones(): Observable<Sancion[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(r => r.map(item => Sancion.fromJSON(item)))
    );
  }
}