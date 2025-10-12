import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ListConstantService {
  private apiUrl = 'http://localhost:8080/constant';

  // listas reactivas para guardar los datos cargados
  private estadosUsuarioSubject = new BehaviorSubject<string[]>([]);
  private rolesSubject = new BehaviorSubject<string[]>([]);
  private estadosPedidoSubject = new BehaviorSubject<string[]>([]);
  private tiposCategoriasSubject = new BehaviorSubject<string[]>([]);
  private estadosArticuloSubject = new BehaviorSubject<string[]>([]);

  // observables para usar en los componentes
  public estadosUsuario$ = this.estadosUsuarioSubject.asObservable();
  public roles$ = this.rolesSubject.asObservable();
  public estadosPedido$ = this.estadosPedidoSubject.asObservable();
  public tiposCategorias$ = this.tiposCategoriasSubject.asObservable();
  public estadosArticulo$ = this.estadosArticuloSubject.asObservable();

  constructor(private http: HttpClient) {
    // carga inicial de todas las constantes al crear el servicio
    this.cargarTodasLasConstantes();
  }

  // carga todas las listas desde el backend
  private cargarTodasLasConstantes(): void {
    this.cargarEstadosUsuario().subscribe();
    this.cargarRoles().subscribe();
    this.cargarEstadosPedido().subscribe();
    this.cargarTiposCategorias().subscribe();
    this.cargarEstadosArticulo().subscribe();
  }

  // obtiene lista de estados de usuario
  cargarEstadosUsuario(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/estados-usuario`).pipe(
      tap(estados => this.estadosUsuarioSubject.next(estados))
    );
  }

  // obtiene lista de roles
  cargarRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/roles`).pipe(
      tap(roles => this.rolesSubject.next(roles))
    );
  }

  // obtiene lista de estados de pedido
  cargarEstadosPedido(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/estados-pedido`).pipe(
      tap(estados => this.estadosPedidoSubject.next(estados))
    );
  }

  // obtiene lista de tipos de categorías
  cargarTiposCategorias(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/tipos-categorias`).pipe(
      tap(tipos => this.tiposCategoriasSubject.next(tipos))
    );
  }

  // obtiene lista de estados de artículo
  cargarEstadosArticulo(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/estados-articulo`).pipe(
      tap(estados => this.estadosArticuloSubject.next(estados))
    );
  }

  // métodos para obtener los valores actuales sincrónicamente
  getEstadosUsuario(): string[] {
    return this.estadosUsuarioSubject.value;
  }

  getRoles(): string[] {
    return this.rolesSubject.value;
  }

  getEstadosPedido(): string[] {
    return this.estadosPedidoSubject.value;
  }

  getTiposCategorias(): string[] {
    return this.tiposCategoriasSubject.value;
  }

  getEstadosArticulo(): string[] {
    return this.estadosArticuloSubject.value;
  }
}
