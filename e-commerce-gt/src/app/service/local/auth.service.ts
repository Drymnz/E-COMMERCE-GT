import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { Usuario } from '../../entities/Usuario';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser: Observable<Usuario | null>;
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    // verificar si se está ejecutando en el navegador
    this.isBrowser = isPlatformBrowser(platformId);

    let storedUser = null;

    // si hay un usuario guardado en localStorage y cargarlo
    if (this.isBrowser) {
      const userString = localStorage.getItem('currentUser');
      if (userString) {
        try {
          const parsedData = JSON.parse(userString);
          storedUser = Usuario.fromJSON(parsedData);
        } catch (error) {
          localStorage.removeItem('currentUser');
        }
      }
    }

    // inicializa el usuario actual
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(storedUser);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  // devuelve el usuario actual
  public get currentUserValue(): Usuario | null {
    return this.currentUserSubject.value;
  }

  // guarda el usuario al iniciar sesión
  login(usuario: Usuario): void {
    if (this.isBrowser) {
      localStorage.setItem('currentUser', JSON.stringify(usuario));
    }
    this.currentUserSubject.next(usuario);
  }

  // elimina el usuario al cerrar sesión
  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);
  }

  // verifica si hay un usuario autenticado
  isAuthenticated(): boolean {
    return this.currentUserValue !== null;
  }

  // verifica si el usuario tiene alguno de los roles permitidos
  hasAnyRole(roles: string[]): boolean {
    const user = this.currentUserValue;
    const userRoleStr = String(user?.id_rol); // Convertir a string
    return user ? roles.includes(userRoleStr) : false;
  }

}
