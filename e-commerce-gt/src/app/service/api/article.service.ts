import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Publicacion } from '../../entities/Publication';

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
    console.log(headers);
    console.log(publicacion.toJSON);
    return this.http.post(`${this.apiUrl}`, publicacion.toJSON(), {
      headers: headers
    });
  }
}