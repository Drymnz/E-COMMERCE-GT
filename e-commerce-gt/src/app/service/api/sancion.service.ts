import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Sancion } from '../../entities/Sancion';

@Injectable({
  providedIn: 'root'
})
export class SancionService {
  private apiUrl = 'http://localhost:8080/sancion';

  constructor(private http: HttpClient) { }

  // Crear nueva sanción
  crearSancion(motivo: string, id_usuario: number): Observable<Sancion> {
    const body = {
      motivo,
      id_usuario,
      fecha_hora: new Date().toISOString()
    };

    return this.http.post<any>(this.apiUrl, body).pipe(
      map(response => Sancion.fromJSON(response))
    );
  }

  // Obtener todas las sanciones
  obtenerTodasSanciones(): Observable<Sancion[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(response => response.map(item => Sancion.fromJSON(item)))
    );
  }

}