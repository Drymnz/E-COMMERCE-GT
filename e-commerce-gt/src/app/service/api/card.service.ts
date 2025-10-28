import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Card } from '../../entities/Card';
import { environment } from './article.service';

@Injectable({
  providedIn: 'root'
})
export class CardService {
  private apiUrl = `${environment.apiUrl}/card`;

  constructor(private http: HttpClient) {}

  registrarTarjeta(card: Card): Observable<Card> {
    return this.http.post<any>(this.apiUrl, card.toJSON()).pipe(
      map(response => Card.fromJSON(response))
    );
  }

  obtenerTarjetasUsuario(idUsuario: number): Observable<Card[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuario/${idUsuario}`).pipe(
      map(response => response.map(card => Card.fromJSON(card)))
    );
  }

  reducirSaldo(numero: string, monto: number): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(
      `${this.apiUrl}/${numero}/reducir-saldo`, 
      null, 
      { params: new HttpParams().set('monto', monto.toString()) }
    );
  }

  eliminarTarjeta(numero: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${numero}`);
  }
}