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
  nombreEmpresa = 'E-Commerce GT';

  constructor(public authService: AuthService, private router: Router) {}

  irHome(): void { this.router.navigate(['/']); }
  irRegistro(): void { this.router.navigate(['/registro']); }
  irLogin(): void { this.router.navigate(['/login']); }
  irAdmin(): void { this.router.navigate(['/admin']); }
  irVentas(): void { this.router.navigate(['/ventas']); }
  irReportes(): void { this.router.navigate(['/report']); }
  irHistorialNotificaciones(): void { this.router.navigate(['/notificacion']); }
  irHistorialEmpleados(): void { this.router.navigate(['/historial-empleados']); }
  irGestionarTrajetas(): void { this.router.navigate(['/gestion-tarjetas']); }
  irGestionarProductos(): void { this.router.navigate(['/manage-products-sale']); }
  irGestionarCarrito(): void { this.router.navigate(['/manage-shopping-cart']); }
  irSeguimientoPedidos(): void { this.router.navigate(['/order-tracking']); }
  irGestorProdutosModedador(): void { this.router.navigate(['/panel-moderator']); }
  irListadoUsuarios(): void { this.router.navigate(['/lits-usuario']); }
  irListadoSnaciones(): void { this.router.navigate(['/lits-sanctions']); }
  irGestionPedidos(): void { this.router.navigate(['/order-management']); }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  getNombreCompleto(): string {
    const user = this.authService.currentUserValue;
    return user ? `${user.nombre} ${user.apellido}` : '';
  }
}