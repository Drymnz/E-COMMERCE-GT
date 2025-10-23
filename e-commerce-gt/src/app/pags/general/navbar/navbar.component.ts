import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../service/local/auth.service';
import { HasRoleDirective } from '../../../directives/has-role.directive';

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
  ) { }

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

  //Cliente
  irGestionarProductos() {
    this.router.navigate(['/manage-products-sale']);
  }
  irGestionarCarrito() {
    this.router.navigate(['/manage-shopping-cart']);
  }

  irSeguimientoPedidos() {
    this.router.navigate(['/report']);
  }

  //Modedador 
  irGestorProdutosModedador() {
    this.router.navigate(['/panel-moderator']);
  }

  irListadoUsuarios() {
    this.router.navigate(['/lits-usuario']);
  }

  irListadoSnaciones() {
    this.router.navigate(['/lits-sanctions']);

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