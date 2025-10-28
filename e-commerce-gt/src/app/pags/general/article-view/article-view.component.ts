import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarritoService } from '../../../service/local/carrito.service';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { Articulo } from '../../../entities/Article';
import { AuthService } from '../../../service/local/auth.service';
import { Router } from '@angular/router';
import { ArticleComponent } from '../article/article.component';

@Component({
  selector: 'app-article-view',
  standalone: true,
  imports: [CommonModule, FormsModule, ArticleComponent],
  templateUrl: './article-view.component.html',
  styleUrls: ['./article-view.component.scss']
})
export class ArticleViewComponent implements OnInit {
  @Input() articulo!: Articulo;
  cantidad = signal(1);
  cantidadInput = 1;
  estadosArticulo: string[] = [];
  mensajeAgregado = signal(false);

  constructor(private carritoService: CarritoService, private constantService: ListConstantService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.constantService.estadosArticulo$.subscribe(estados => this.estadosArticulo = estados);
  }

  getNombreEstado(): string {
    if (this.articulo.id_estado_articulo > 0 && this.articulo.id_estado_articulo <= this.estadosArticulo.length) {
      return this.estadosArticulo[this.articulo.id_estado_articulo - 1];
    }
    return 'Desconocido';
  }

  incrementarCantidad(): void {
    if (this.cantidad() < this.articulo.stock) {
      this.cantidad.update(c => c + 1);
      this.cantidadInput = this.cantidad();
    }
  }

  decrementarCantidad(): void {
    if (this.cantidad() > 1) {
      this.cantidad.update(c => c - 1);
      this.cantidadInput = this.cantidad();
    }
  }

  onCantidadChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const valor = parseInt(input.value, 10);
    if (!isNaN(valor) && valor > 0) {
      const nuevaCantidad = Math.min(valor, this.articulo.stock);
      this.cantidad.set(nuevaCantidad);
      this.cantidadInput = nuevaCantidad;
    } else {
      this.cantidad.set(1);
      this.cantidadInput = 1;
    }
  }

  agregarAlCarrito(): void {
    if (this.authService.isAuthenticated()) {
      if (this.articulo.disponible && this.cantidad() > 0) {
        this.carritoService.agregarArticulo(this.articulo, this.cantidad());
        this.mensajeAgregado.set(true);
        this.cantidad.set(1);
        this.cantidadInput = 1;
        setTimeout(() => {
          this.mensajeAgregado.set(false);
        }, 3000);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  cerrarMensaje(): void {
    this.mensajeAgregado.set(false);
  }

  getStockBadgeClass(): string {
    if (this.articulo.stock === 0) return 'bg-danger';
    if (this.articulo.stock < 10) return 'bg-warning text-dark';
    return 'bg-success';
  }
}