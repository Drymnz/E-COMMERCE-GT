import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { Articulo } from '../../../entities/Customer';
import { ArticleComponent } from '../article/article.component';
import { ArticleService } from '../../../service/api/article.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ArticleComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  categorias: string[] = [];
  estadoArticulo: string[] = [];
  articulos: Articulo[] = [];

  constructor(
    private constantService: ListConstantService,
    private listArticle:ArticleService
  ) { }

  ngOnInit(): void {
    // Obtener listas de categorías
    this.constantService.tiposCategorias$.subscribe(categorias => {
      this.categorias = categorias;
    });

    // Obtener listas de estado articulo
    this.constantService.estadosArticulo$.subscribe(estadoArticulo => {
      this.estadoArticulo = estadoArticulo;
      // Cargar artículos después de obtener los estados
      if (this.estadoArticulo.length > 0 && this.articulos.length === 0) {
        this.cargarArticulos();
      }
    });
  }

  private cargarArticulos(): void {
  this.listArticle.getAvailableArticles().subscribe({
    next: (articulos) => {
      this.articulos = articulos;
    },
    error: (error) => {
      console.error('Error al cargar artículos:', error);
    }
  });
}

  // Obtiene el nombre del estado del artículo según su id
  getEstadoNombre(id_estado: number): string {
    if (!this.estadoArticulo || this.estadoArticulo.length === 0) {
      return 'Sin estado';
    }

    const index = id_estado - 1;
    return this.estadoArticulo[index] || 'Desconocido';
  }

  // Determina si el artículo está disponible basándose en stock
  isArticuloDisponible(articulo: Articulo): boolean {
    return articulo.stock > 0;
  }

  onAgregarAlCarrito(articulo: Articulo): void {
    console.log('Agregar al carrito:', articulo);
    alert(`${articulo.nombre} agregado al carrito`);
  }

  onVerDetalles(articulo: Articulo): void {
    console.log('Ver detalles:', articulo);
    alert(`Ver detalles de: ${articulo.nombre}`);
  }
}