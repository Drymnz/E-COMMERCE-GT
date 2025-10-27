import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Publicacion } from '../../entities/Publication';
import { Articulo } from '../../entities/Article';


export const environment = {
  production: false,
  apiUrl: 'https://12d306a207fe.ngrok-free.app'
};

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
 private apiUrl = `${environment.apiUrl}/article`; 

  constructor(private http: HttpClient) { }

  // Crear una nueva publicación, es decir un nuevo articulo 
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

  // Obtener un artículo específico de un usuario
  getArticleByUserAndId(idUsuario: number, idArticulo: number): Observable<Articulo | null> {
    return this.http.get<any>(`${this.apiUrl}/user/${idUsuario}/article/${idArticulo}`).pipe(
      map(response => {
        if (response.data) {
          return Articulo.fromJSON(response.data);
        }
        return null;
      })
    );
  }

  // Actualizar un artículo completo
  updateArticle(idArticulo: number, articulo: Articulo): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.put(`${this.apiUrl}/${idArticulo}`, articulo.toJSON(), {
      headers: headers
    });
  }

  // Obtener un artículo por su ID
  getArticleById(idArticulo: number): Observable<Articulo | null> {
    return this.http.get<any>(`${this.apiUrl}/details/${idArticulo}`).pipe(
      map(response => {
        if (response.data) {
          return Articulo.fromJSON(response.data);
        }
        return null;
      })
    );
  }
}