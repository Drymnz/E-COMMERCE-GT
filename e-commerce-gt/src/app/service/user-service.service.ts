import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Usuario } from '../entities/Usuario';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) { }

  /**
   * @param email - Correo electrónico del usuario
   * @param password - Contraseña del usuario
   */
  login(email: string, password: string): Observable<Usuario> {
    const params = new HttpParams()
      .set('email', email)
      .set('password', password);

    return this.http.get<any>(`${this.apiUrl}/login`, { params }).pipe(
      map(response => Usuario.crearDesdeDatos(
        response.id_usuario,
        response.nombre,
        response.apellido,
        response.email,
        response.id_estado,
        response.id_rol
      ))
    );
  }

  /**
   * @param nombre - Nombre del usuario
   * @param apellido - Apellido del usuario
   * @param email - Correo electrónico
   * @param password - Contraseña
   * @param id_rol - Rol del usuario 
   */
  crearUsuario(nombre: string, apellido: string, email: string, password: string, id_rol: string = '1', id_estado: string = '2'): Observable<Usuario> {
    const body = {
      nombre,
      apellido,
      email,
      password, 
      id_estado,
      id_rol
    };

    return this.http.post<any>(this.apiUrl, body).pipe(
      map(response => Usuario.crearDesdeDatos(
        response.id_usuario,
        response.nombre,
        response.apellido,
        response.email,
        response.id_estado,
        response.id_rol
      ))
    );
  }

  /**
   * @param email - Correo electrónico del usuario
   * @returns Observable<Usuario> - Usuario encontrado
   */
  buscarUsuario(email: string): Observable<Usuario> {
    const params = new HttpParams().set('email', email);

    return this.http.get<any>(`${this.apiUrl}/buscar`, { params }).pipe(
      map(response => Usuario.crearDesdeDatos(
        response.id_usuario,
        response.nombre,
        response.apellido,
        response.email,
        response.id_estado,
        response.id_rol
      ))
    );
  }

  /**
   * @param usuario - Usuario con los datos actualizados
   * @returns Observable<Usuario> - Usuario actualizado
   */
  modificarUsuario(usuario: Usuario): Observable<Usuario> {
    const body = {
      id_usuario: usuario.id_usuario,
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      email: usuario.email,
      id_estado: usuario.id_estado,
      id_rol: usuario.id_rol
    };

    return this.http.put<any>(this.apiUrl, body).pipe(
      map(response => Usuario.crearDesdeDatos(
        response.id_usuario,
        response.nombre,
        response.apellido,
        response.email,
        response.id_estado,
        response.id_rol
      ))
    );
  }
}