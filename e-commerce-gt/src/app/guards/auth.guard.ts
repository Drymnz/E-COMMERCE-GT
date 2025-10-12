import { inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../service/auth.service';

// Guard básico para autenticación
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // Evitar error si no es navegador
  if (!isPlatformBrowser(platformId)) return true;

  // Si hay login permitir
  if (authService.isAuthenticated()) return true;

  // Si no hay login redireccionar
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};