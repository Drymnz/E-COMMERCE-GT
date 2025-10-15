import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CarritoService } from '../../../service/local/carrito.service';
import { ItemCarrito } from '../../../entities/ItemCarrito';

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './manage-shopping-cart.component.html',
  styleUrl: './manage-shopping-cart.component.scss'
})
export class ManageShoppingCartComponent {
  mensajeExito: string | null = null;
  mensajeError: string | null = null;

  constructor(public carritoService: CarritoService) {}

  incrementarCantidad(item: ItemCarrito): void {
    if (item.cantidad < item.articulo.stock) {
      this.carritoService.actualizarCantidad(item.articulo.id_articulo, item.cantidad + 1);
    } else {
      this.mostrarMensajeError('No hay más stock disponible');
    }
  }

  decrementarCantidad(item: ItemCarrito): void {
    if (item.cantidad > 1) {
      this.carritoService.actualizarCantidad(item.articulo.id_articulo, item.cantidad - 1);
    }
  }

  eliminarItem(idArticulo: number): void {
    this.carritoService.eliminarArticulo(idArticulo);
    this.mostrarMensajeExito('Artículo eliminado del carrito');
  }

  vaciarCarrito(): void {
    if (confirm('¿Estás seguro de vaciar el carrito?')) {
      this.carritoService.vaciarCarrito();
      this.mostrarMensajeExito('Carrito vaciado');
    }
  }

  procederPago(): void {
    if (this.carritoService.totalItems() === 0) {
      this.mostrarMensajeError('El carrito está vacío');
      return;
    }
    // Implementar lógica de pago
  }

  private mostrarMensajeExito(mensaje: string): void {
    this.mensajeExito = mensaje;
    setTimeout(() => this.mensajeExito = null, 3000);
  }

  private mostrarMensajeError(mensaje: string): void {
    this.mensajeError = mensaje;
    setTimeout(() => this.mensajeError = null, 3000);
  }

  cerrarMensaje(tipo: 'exito' | 'error'): void {
    if (tipo === 'exito') this.mensajeExito = null;
    else this.mensajeError = null;
  }
}