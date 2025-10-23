import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Articulo } from '../../../../entities/Customer';

@Component({
  selector: 'app-product-card-manage',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card-manage.component.html',
  styleUrls: ['./product-card-manage.component.scss']
})
export class ProductCardManageComponent {
  @Input() articulo!: Articulo;
  @Input() estadoNombre: string = 'Sin estado';
  @Input() estadoModeracion: string = 'Pendiente'; 
  
  @Output() editar = new EventEmitter<Articulo>();
  @Output() eliminar = new EventEmitter<Articulo>();
  @Output() cambiarEstado = new EventEmitter<Articulo>();

  imagenError: boolean = false;

  get imagenUrl(): string {
    if (!this.articulo.imagen || this.imagenError) {
      return 'https://via.placeholder.com/300x200?text=Sin+Imagen';
    }
    return this.articulo.imagen;
  }

  get estadoVenta(): string {
    return this.articulo.stock > 0 ? 'En venta' : 'Vendido';
  }

  get estadoVentaClass(): string {
    return this.articulo.stock > 0 ? 'bg-success' : 'bg-secondary';
  }

  // NUEVO 
  get estadoModeracionClass(): string {
    const idAccion = this.articulo.id_accion;
    if (idAccion === 1) return 'bg-warning text-dark'; // Pendiente
    if (idAccion === 2) return 'bg-success'; // Aprobado
    if (idAccion === 3) return 'bg-danger'; // Rechazado
    return 'bg-secondary';
  }

  get stockClass(): string {
    if (this.articulo.stock === 0) return 'text-danger';
    if (this.articulo.stock < 10) return 'text-warning';
    return 'text-success';
  }

  onImagenError(): void {
    this.imagenError = true;
  }

  editarProducto(): void {
    this.editar.emit(this.articulo);
  }

  eliminarProducto(): void {
    this.eliminar.emit(this.articulo);
  }

  cambiarEstadoProducto(): void {
    this.cambiarEstado.emit(this.articulo);
  }
}