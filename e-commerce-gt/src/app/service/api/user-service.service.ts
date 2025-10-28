import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Usuario } from '../../entities/Usuario';
import { PaginatedResponse } from '../../entities/PaginatedResponse';
import { environment } from './article.service';

export interface UsuarioDetalle {
  usuario: Usuario;
  cantidadSanciones: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/user`;

  constructor(private http: HttpClient) { }

  private mapUsuario(u: any): Usuario {
    return Usuario.crearDesdeDatos(
      u.id_usuario,
      u.nombre,
      u.apellido,
      u.email,
      u.id_estado.toString(),
      u.id_rol.toString()
    );
  }

  obtenerUsuariosPaginados(page: number = 1, pageSize: number = 10): Observable<PaginatedResponse> {
    const params = new HttpParams().set('page', page.toString()).set('pageSize', pageSize.toString());

    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map(r => PaginatedResponse.crearDesdeDatos(
        r.usuarios.map((u: any) => this.mapUsuario(u)),
        r.currentPage,
        r.pageSize,
        r.totalUsuarios,
        r.totalPages
      ))
    );
  }

  obtenerUsuarioConSanciones(idUsuario: number): Observable<UsuarioDetalle> {
    return this.http.get<any>(`${this.apiUrl}/${idUsuario}/detalle`).pipe(
      map(r => ({
        usuario: this.mapUsuario(r.usuario),
        cantidadSanciones: r.cantidadSanciones
      }))
    );
  }

  login(email: string, password: string): Observable<Usuario> {
    const params = new HttpParams().set('email', email).set('password', password);
    return this.http.get<any>(`${this.apiUrl}/login`, { params }).pipe(map(r => this.mapUsuario(r)));
  }

  // Crear usuario (versión completa con id_estado)
  crearUsuario(
    nombre: string,
    apellido: string,
    email: string,
    password: string,
    id_rol: number,
    id_estado: number
  ): Observable<Usuario>;

  // Crear usuario cliente id_rol=1, id_estado=2
  crearUsuario(
    nombre: string,
    apellido: string,
    email: string,
    password: string
  ): Observable<Usuario>;

  crearUsuario(
    nombre: string,
    apellido: string,
    email: string,
    password: string,
    id_rol: number = 1,
    id_estado: number = 2
  ): Observable<Usuario> {
    return this.http.post<any>(this.apiUrl, {
      nombre,
      apellido,
      email,
      password,
      id_estado,
      id_rol
    }).pipe(map(r => this.mapUsuario(r)));
  }

  buscarUsuario(email: string): Observable<Usuario> {
    return this.http.get<any>(`${this.apiUrl}/buscar`, { 
      params: new HttpParams().set('email', email) 
    }).pipe(map(r => this.mapUsuario(r)));
  }

  modificarUsuario(usuario: Usuario): Observable<Usuario> {
    return this.http.put<any>(this.apiUrl, {
      id_usuario: usuario.id_usuario,
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      email: usuario.email,
      id_estado: Number(usuario.id_estado),
      id_rol: Number(usuario.id_rol)
    }).pipe(map(r => this.mapUsuario(r)));
  }

  obtenerEmpleados(): Observable<Usuario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/empleados`).pipe(
      map(r => r.map(u => this.mapUsuario(u)))
    );
  }
}