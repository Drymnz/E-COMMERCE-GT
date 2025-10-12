import { inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../service/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // evitar error si no es navegador
  if (!isPlatformBrowser(platformId)) return true;

  // si hay login permitir
  if (authService.isAuthenticated()) return true;

  // si no hay login redireccionar
  router.navigate(['/'], { queryParams: { returnUrl: state.url } });
  return false;
};

// validar acceso por roles
export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const platformId = inject(PLATFORM_ID);

    // evitar error si no es navegador
    if (!isPlatformBrowser(platformId)) return true;

    // si no hay login redireccionar
    if (!authService.isAuthenticated()) {
      router.navigate(['/']);
      return false;
    }

    // si tiene rol permitido permitir
    if (authService.hasAnyRole(allowedRoles)) return true;

    // si no tiene rol redireccionar
    router.navigate(['/']);
    return false;
  };
};
