import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  imports: [CommonModule, FormsModule, ArticleComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  categorias: string[] = [];
  estadoArticulo: string[] = [];
  articulos: Articulo[] = [];
  articulosFiltrados: Articulo[] = [];
  articulosPaginados: Articulo[] = [];
  
  // Filtros
  categoriaSeleccionada: string | null = null;
  terminoBusqueda: string = '';
  
  // Paginación
  paginaActual: number = 1;
  articulosPorPagina: number = 6;
  totalPaginas: number = 1;

  // Exponer Math para usar en el template
  Math = Math;

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
      next: (articulos) => {
        this.articulos = articulos;
        this.aplicarFiltros();
      },
      error: (error) => console.error('Error al cargar artículos:', error)
    });
  }

  seleccionarCategoria(categoria: string | null): void {
    this.categoriaSeleccionada = categoria;
    this.paginaActual = 1;
    this.aplicarFiltros();
  }

  buscar(termino: string): void {
    this.terminoBusqueda = termino;
    this.paginaActual = 1;
    this.aplicarFiltros();
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.paginaActual = 1;
    this.aplicarFiltros();
  }

  private aplicarFiltros(): void {
    let resultado = [...this.articulos];

    // Filtrar por categoría
    if (this.categoriaSeleccionada) {
      resultado = resultado.filter(articulo => 
        articulo.categorias.includes(this.categoriaSeleccionada!)
      );
    }

    // Filtrar por búsqueda (nombre o descripción)
    if (this.terminoBusqueda.trim()) {
      const termino = this.terminoBusqueda.toLowerCase().trim();
      resultado = resultado.filter(articulo => 
        articulo.nombre.toLowerCase().includes(termino) ||
        articulo.descripcion.toLowerCase().includes(termino)
      );
    }

    this.articulosFiltrados = resultado;

    // Calcular paginación
    this.calcularPaginacion();
    this.actualizarArticulosPaginados();
  }

  private calcularPaginacion(): void {
    this.totalPaginas = Math.ceil(this.articulosFiltrados.length / this.articulosPorPagina);
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = 1;
    }
  }

  private actualizarArticulosPaginados(): void {
    const inicio = (this.paginaActual - 1) * this.articulosPorPagina;
    const fin = inicio + this.articulosPorPagina;
    this.articulosPaginados = this.articulosFiltrados.slice(inicio, fin);
  }

  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
      this.actualizarArticulosPaginados();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  getPaginasArray(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  getPaginasVisibles(): number[] {
    const maxPaginasVisibles = 5;
    const mitad = Math.floor(maxPaginasVisibles / 2);
    
    let inicio = Math.max(1, this.paginaActual - mitad);
    let fin = Math.min(this.totalPaginas, inicio + maxPaginasVisibles - 1);
    
    if (fin - inicio < maxPaginasVisibles - 1) {
      inicio = Math.max(1, fin - maxPaginasVisibles + 1);
    }
    
    const paginas = [];
    for (let i = inicio; i <= fin; i++) {
      paginas.push(i);
    }
    return paginas;
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