import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Articulo } from '../../../entities/Customer';

@Component({
  selector: 'app-article',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './article.component.html',
  styleUrls: ['./article.component.scss']
})
export class ArticleComponent {
  @Input({ required: false }) articulo: Articulo | null = null;
  @Input() mostrarAcciones: boolean = true;
  @Input() estadoNombre: string = 'Sin estado';
  @Input() disponible: boolean = false;

  @Output() agregarCarrito = new EventEmitter<Articulo>();
  @Output() verDetalles = new EventEmitter<Articulo>();

  imagenError: boolean = false;

  get imagenUrl(): string {
    if (!this.articulo || !this.articulo.imagen || this.imagenError) {
      return 'load.jpg';
    }
    return this.articulo.imagen;
  }

  get nombreArticulo(): string {
    return this.articulo?.nombre || 'Artículo no cargado';
  }

  get descripcionArticulo(): string {
    return this.articulo?.descripcion || 'Sin descripción disponible';
  }

  get precioArticulo(): string {
    return this.articulo?.precioFormateado || 'Q 0.00';
  }

  get stockArticulo(): number {
    return this.articulo?.stock || 0;
  }

  get categoriasArticulo(): string[] {
    return this.articulo?.categorias || [];
  }

  //Verde si disponible, Rojo si no disponible
  get disponibilidadClass(): string {
    if (!this.articulo || this.articulo.stock <= 0) {
      return 'bg-danger'; // Rojo
    }
    return 'bg-success'; // Verde
  }

  // Obtiene el texto de disponibilidad
  get disponibilidadTexto(): string {
    if (!this.articulo || this.articulo.stock <= 0) {
      return 'No disponible';
    }
    return 'Disponible';
  }

  onImagenError(): void {
    this.imagenError = true;
  }

  agregarAlCarrito(): void {
    if (this.articulo && this.disponible) {
      this.agregarCarrito.emit(this.articulo);
    }
  }

  verDetallesArticulo(): void {
    if (this.articulo) {
      this.verDetalles.emit(this.articulo);
    }
  }
}