import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Usuario } from '../../entities/Usuario';
import { PaginatedResponse } from '../../entities/PaginatedResponse';

export interface UsuarioDetalle {
  usuario: Usuario;
  cantidadSanciones: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) { }

  // Obtener usuarios con paginación
  obtenerUsuariosPaginados(page: number = 1, pageSize: number = 10): Observable<PaginatedResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map(response => {
        const usuarios = response.usuarios.map((u: any) => Usuario.crearDesdeDatos(
          u.id_usuario,
          u.nombre,
          u.apellido,
          u.email,
          u.id_estado.toString(),
          u.id_rol.toString()
        ));

        return PaginatedResponse.crearDesdeDatos(
          usuarios,
          response.currentPage,
          response.pageSize,
          response.totalUsuarios,
          response.totalPages
        );
      })
    );
  }

  // Obtener usuario con detalle de sanciones
  obtenerUsuarioConSanciones(idUsuario: number): Observable<UsuarioDetalle> {
    return this.http.get<any>(`${this.apiUrl}/${idUsuario}/detalle`).pipe(
      map(response => ({
        usuario: Usuario.crearDesdeDatos(
          response.usuario.id_usuario,
          response.usuario.nombre,
          response.usuario.apellido,
          response.usuario.email,
          response.usuario.id_estado.toString(),
          response.usuario.id_rol.toString()
        ),
        cantidadSanciones: response.cantidadSanciones
      }))
    );
  }

  // Login de usuario
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

  // Implementación
  crearUsuario(
    nombre: string,
    apellido: string,
    email: string,
    password: string,
    id_rol: number = 1,
    id_estado: number = 2
  ): Observable<Usuario> {
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
        response.id_estado.toString(),
        response.id_rol.toString()
      ))
    );
  }

  // Buscar usuario por email
  buscarUsuario(email: string): Observable<Usuario> {
    const params = new HttpParams().set('email', email);

    return this.http.get<any>(`${this.apiUrl}/buscar`, { params }).pipe(
      map(response => Usuario.crearDesdeDatos(
        response.id_usuario,
        response.nombre,
        response.apellido,
        response.email,
        response.id_estado.toString(),
        response.id_rol.toString()
      ))
    );
  }

  // Modificar usuario existente
  modificarUsuario(usuario: Usuario): Observable<Usuario> {
    const body = {
      id_usuario: usuario.id_usuario,
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      email: usuario.email,
      id_estado: Number(usuario.id_estado),
      id_rol: Number(usuario.id_rol)
    };

    return this.http.put<any>(this.apiUrl, body).pipe(
      map(response => Usuario.crearDesdeDatos(
        response.id_usuario,
        response.nombre,
        response.apellido,
        response.email,
        response.id_estado.toString(),
        response.id_rol.toString()
      ))
    );
  }

  // Historial de empleados
  obtenerEmpleados(): Observable<Usuario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/empleados`).pipe(
      map(response => response.map(u => Usuario.crearDesdeDatos(
        u.id_usuario,
        u.nombre,
        u.apellido,
        u.email,
        u.id_estado.toString(),
        u.id_rol.toString()
      )))
    );
  }
}