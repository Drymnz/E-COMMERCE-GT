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
  ) {}

  ngOnInit(): void {
    this.constantService.tiposCategorias$.subscribe(categorias => this.categorias = categorias);
    this.constantService.estadosArticulo$.subscribe(estadoArticulo => {
      this.estadoArticulo = estadoArticulo;
      if (estadoArticulo.length > 0 && this.articulos.length === 0) this.cargarArticulos();
    });
  }

  private cargarArticulos(): void {
    this.listArticle.getAvailableArticles().subscribe({
      next: (articulos) => this.articulos = articulos,
      error: (error) => console.error('Error al cargar artículos:', error)
    });
  }

  getEstadoNombre(id_estado: number): string {
    return this.estadoArticulo?.length > 0 ? this.estadoArticulo[id_estado - 1] || 'Desconocido' : 'Sin estado';
  }

  isArticuloDisponible(articulo: Articulo): boolean {
    return articulo.stock > 0;
  }

  onAgregarAlCarrito(articulo: Articulo): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    if (articulo.disponible && articulo.stock > 0) {
      this.carritoService.agregarArticulo(articulo, 1);
      this.router.navigate(['/manage-shopping-cart']);
    }
  }

  onVerDetalles(articulo: Articulo): void {
    this.router.navigate(['/see-product', articulo.id_articulo]);
  }
}