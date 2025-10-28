import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Publicacion } from '../../entities/Publication';
import { Articulo } from '../../entities/Article';

export const environment = {
  production: false,
  apiUrl: 'https://scapose-annmarie-heterozygous.ngrok-free.dev'
};

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private apiUrl = `${environment.apiUrl}/article`;
  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) {}

  createPublicacion(publicacion: Publicacion): Observable<any> {
    return this.http.post(this.apiUrl, publicacion.toJSON(), { headers: this.headers });
  }

  getAvailableArticles(): Observable<Articulo[]> {
    return this.http.get<any>(`${this.apiUrl}/available`).pipe(
      map(response => response.data && Array.isArray(response.data) 
        ? response.data.map((item: any) => Articulo.fromJSON(item)) 
        : [])
    );
  }

  getArticlesByUserId(idUsuario: number): Observable<Articulo[]> {
    return this.http.get<any>(`${this.apiUrl}/user/${idUsuario}`).pipe(
      map(response => response.data && Array.isArray(response.data)
        ? response.data.map((item: any) => Articulo.fromJSON(item))
        : [])
    );
  }

  deleteArticle(idArticulo: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${idArticulo}`);
  }

  updateArticleStatus(idArticulo: number, idEstadoArticulo: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${idArticulo}/status`, { id_estado_articulo: idEstadoArticulo });
  }

  getArticleByUserAndId(idUsuario: number, idArticulo: number): Observable<Articulo | null> {
    return this.http.get<any>(`${this.apiUrl}/user/${idUsuario}/article/${idArticulo}`).pipe(
      map(response => response.data ? Articulo.fromJSON(response.data) : null)
    );
  }

  updateArticle(idArticulo: number, articulo: Articulo): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idArticulo}`, articulo.toJSON(), { headers: this.headers });
  }

  getArticleById(idArticulo: number): Observable<Articulo | null> {
    return this.http.get<any>(`${this.apiUrl}/details/${idArticulo}`).pipe(
      map(response => response.data ? Articulo.fromJSON(response.data) : null)
    );
  }
}