import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { Articulo } from '../../../entities/Article';
import { ArticleComponent } from '../article/article.component';
import { ArticleService } from '../../../service/api/article.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../service/local/auth.service';
import { CarritoService } from '../../../service/local/carrito.service';

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
    private carritoService: CarritoService,
    private constantService: ListConstantService,
    private listArticle: ArticleService,
    private router: Router,
    private authService: AuthService
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
    if (this.authService.isAuthenticated()) {
      if (articulo.disponible && articulo.stock > 0) {
        this.carritoService.agregarArticulo(articulo, 1);
        this.router.navigate(['/manage-shopping-cart']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  onVerDetalles(articulo: Articulo): void {
    this.router.navigate(['/see-product', articulo.id_articulo]);
  }
}