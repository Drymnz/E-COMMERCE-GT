import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { HasRoleDirective } from '../../directives/has-role.directive';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, HasRoleDirective],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  nombreEmpresa: string = 'E-Commerce GT';

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  irHome(): void {
    this.router.navigate(['/']);
  }

  irRegistro(): void {
    this.router.navigate(['/registro']);
  }

  irLogin(): void {
    this.router.navigate(['/login']);
  }

  irAdmin(): void {
    this.router.navigate(['/admin']);
  }

  irReportes(): void {
    this.router.navigate(['/report']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  getNombreCompleto(): string {
    const user = this.authService.currentUserValue;
    return user ? `${user.nombre} ${user.apellido}` : '';
  }
}