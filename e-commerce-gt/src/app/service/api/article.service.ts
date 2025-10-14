import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Publicacion } from '../../entities/Publication';
import { Articulo } from '../../entities/Customer';

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private apiUrl = 'http://localhost:8080/article';

  constructor(private http: HttpClient) { }

  // Crear una nueva publicación (artículo + publicación)
  createPublicacion(publicacion: Publicacion): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.apiUrl}`, publicacion.toJSON(), {
      headers: headers
    });
  }

  // Obtener listado de todos los articulos disponibles
  getAvailableArticles(): Observable<Articulo[]> {
    return this.http.get<any>(`${this.apiUrl}/available`).pipe(
      map(response => {
        if (response.data && Array.isArray(response.data)) {
          return response.data.map((item: any) => Articulo.fromJSON(item));
        }
        return [];
      })
    );
  }

  // Obtener artículos de un usuario específico
  getArticlesByUserId(idUsuario: number): Observable<Articulo[]> {
    return this.http.get<any>(`${this.apiUrl}/user/${idUsuario}`).pipe(
      map(response => {
        if (response.data && Array.isArray(response.data)) {
          return response.data.map((item: any) => Articulo.fromJSON(item));
        }
        return [];
      })
    );
  }

  // Eliminar un artículo
  deleteArticle(idArticulo: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${idArticulo}`);
  }

  // Actualizar estado de un artículo
  updateArticleStatus(idArticulo: number, idEstadoArticulo: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${idArticulo}/status`, {
      id_estado_articulo: idEstadoArticulo
    });
  }
}