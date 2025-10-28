import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from './article.service';

@Injectable({
  providedIn: 'root'
})
export class ListConstantService {
  private apiUrl = `${environment.apiUrl}/constant`;

  // listas reactivas para guardar los datos cargados
  private estadosUsuarioSubject = new BehaviorSubject<string[]>([]);
  private rolesSubject = new BehaviorSubject<string[]>([]);
  private estadosPedidoSubject = new BehaviorSubject<string[]>([]);
  private tiposCategoriasSubject = new BehaviorSubject<string[]>([]);
  private estadosArticuloSubject = new BehaviorSubject<string[]>([]);
  private estadosModeracionSubject = new BehaviorSubject<string[]>([]);

  // observables para usar en los componentes
  public estadosUsuario$ = this.estadosUsuarioSubject.asObservable();
  public roles$ = this.rolesSubject.asObservable();
  public estadosPedido$ = this.estadosPedidoSubject.asObservable();
  public tiposCategorias$ = this.tiposCategoriasSubject.asObservable();
  public estadosArticulo$ = this.estadosArticuloSubject.asObservable();
  public estadosModeracion$ = this.estadosModeracionSubject.asObservable();

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
    this.cargarEstadosModeracion().subscribe();
  }

  private cargarLista(endpoint: string, subject: BehaviorSubject<string[]>): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/${endpoint}`).pipe(
      tap(data => subject.next(data))
    );
  }

  cargarEstadosUsuario(): Observable<string[]> {
    return this.cargarLista('estados-usuario', this.estadosUsuarioSubject);
  }

  cargarRoles(): Observable<string[]> {
    return this.cargarLista('roles', this.rolesSubject);
  }

  cargarEstadosPedido(): Observable<string[]> {
    return this.cargarLista('estados-pedido', this.estadosPedidoSubject);
  }

  cargarTiposCategorias(): Observable<string[]> {
    return this.cargarLista('tipos-categorias', this.tiposCategoriasSubject);
  }

  cargarEstadosArticulo(): Observable<string[]> {
    return this.cargarLista('estados-articulo', this.estadosArticuloSubject);
  }

  cargarEstadosModeracion(): Observable<string[]> {
    return this.cargarLista('estados-moderacion', this.estadosModeracionSubject);
  }

  // obtener los valores actuales sincrónicamente
  getEstadosModeracion(): string[] { return this.estadosModeracionSubject.value; }
  getEstadosUsuario(): string[] { return this.estadosUsuarioSubject.value; }
  getRoles(): string[] { return this.rolesSubject.value; }
  getEstadosPedido(): string[] { return this.estadosPedidoSubject.value; }
  getTiposCategorias(): string[] { return this.tiposCategoriasSubject.value; }
  getEstadosArticulo(): string[] { return this.estadosArticuloSubject.value; }
}