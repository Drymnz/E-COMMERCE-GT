import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Articulo } from '../../../entities/Article';
import { ModalSelectOptionComponent } from '../../general/modal-select-option/modal-select-option.component';
import { NotifyConfirmComponent } from '../../general/notify-confirm/notify-confirm.component';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { AuthService } from '../../../service/local/auth.service';
import { ArticleService } from '../../../service/api/article.service';
import { ProductCardManageComponent } from './product-card-manage/product-card-manage.component';

@Component({
  selector: 'app-manage-products-sale',
  standalone: true,
  imports: [CommonModule, ProductCardManageComponent, ModalSelectOptionComponent, NotifyConfirmComponent],
  templateUrl: './manage-products-sale.component.html',
  styleUrls: ['./manage-products-sale.component.scss']
})
export class ManageProductsSaleComponent implements OnInit {
  articulos: Articulo[] = [];
  categorias: string[] = [];
  estadosArticulo: string[] = [];
  estadosModeracion: string[] = []; 

  filtroSeleccionado: string = 'todos';
  cargando: boolean = false;

  mostrarModalEstado: boolean = false;
  articuloSeleccionado: Articulo | null = null;

  mostrarModalEliminar: boolean = false;
  articuloAEliminar: Articulo | null = null;

  constructor(
    private router: Router,
    private constantService: ListConstantService,
    private authService: AuthService,
    private articleService: ArticleService
  ) { }

  ngOnInit(): void {
    this.cargarConstantes();
    this.cargarArticulos();
  }

  cargarConstantes(): void {
    this.constantService.tiposCategorias$.subscribe(categorias => {
      this.categorias = categorias;
    });

    this.constantService.estadosArticulo$.subscribe(estados => {
      this.estadosArticulo = estados;
    });

    this.constantService.estadosModeracion$.subscribe(estados => {
      this.estadosModeracion = estados;
    });
  }

  cargarArticulos(): void {
    this.cargando = true;
    const id_user = this.authService.currentUserValue?.id_usuario;

    if (id_user) {
      this.articleService.getArticlesByUserId(id_user).subscribe({
        next: (articulos) => {
          this.articulos = articulos;
          this.cargando = false;
        },
        error: (error) => {
          console.error('Error al cargar artículos del usuario:', error);
          this.articulos = [];
          this.cargando = false;
        }
      });
    } else {
      console.error('No hay usuario autenticado');
      this.articulos = [];
      this.cargando = false;
    }
  }

  get articulosFiltrados(): Articulo[] {
    if (this.filtroSeleccionado === 'todos') {
      return this.articulos;
    } else if (this.filtroSeleccionado === 'venta') {
      return this.articulos.filter(a => a.stock > 0);
    } else if (this.filtroSeleccionado === 'vendidos') {
      return this.articulos.filter(a => a.stock === 0);
    }
    return this.articulos;
  }

  get totalProductos(): number {
    return this.articulos.length;
  }

  get productosEnVenta(): number {
    return this.articulos.filter(a => a.stock > 0).length;
  }

  get productosVendidos(): number {
    return this.articulos.filter(a => a.stock === 0).length;
  }

  obtenerEstadoNombre(idEstado: number): string {
    const indice = idEstado - 1 ;
    if (indice >= 0 && indice < this.estadosArticulo.length) {
      return this.estadosArticulo[indice];
    }
    return 'Sin estado';
  }

  obtenerEstadoModeracion(idAccion: number): string {
    const indice = idAccion - 1;
    if (indice >= 0 && indice < this.estadosModeracion.length) {
      return this.estadosModeracion[indice];
    }
    return 'Pendiente';
  }


  irAPublicar(): void {
    this.router.navigate(['/register-article']);
  }

  editarProducto(articulo: Articulo): void {
    this.router.navigate(['/register-article', articulo.id_articulo]);
  }

  eliminarProducto(articulo: Articulo): void {
    this.articuloAEliminar = articulo;
    this.mostrarModalEliminar = true;
  }

  confirmarEliminar(confirmado: boolean): void {
    if (confirmado && this.articuloAEliminar) {
      this.articleService.deleteArticle(this.articuloAEliminar.id_articulo).subscribe({
        next: (response) => {
          this.articulos = this.articulos.filter(a => a.id_articulo !== this.articuloAEliminar!.id_articulo);
          this.cerrarModalEliminar();
        },
        error: (error) => {
          console.error('Error al eliminar artículo:', error);
          // Mostrar mensaje de error al usuario
        }
      });
    }
  }

  cerrarModalEliminar(): void {
    this.mostrarModalEliminar = false;
    this.articuloAEliminar = null;
  }

  cambiarEstadoProducto(articulo: Articulo): void {
    this.articuloSeleccionado = articulo;
    this.mostrarModalEstado = true;
  }

  confirmarCambioEstado(nuevoEstado: string): void {
    if (this.articuloSeleccionado) {
      const indiceEstado = this.estadosArticulo.indexOf(nuevoEstado);
      const nuevoIdEstado = indiceEstado + 1;
      const idArticulo = this.articuloSeleccionado.id_articulo; 
      this.articuloSeleccionado.id_estado_articulo = nuevoIdEstado-2;
      this.articleService.updateArticleStatus(idArticulo, nuevoIdEstado).subscribe({
        next: (response) => {
          // Buscar el artículo en el array y actualizar su estado
          const articulo = this.articulos.find(a => a.id_articulo === idArticulo);
          if (articulo) {
            articulo.id_estado_articulo = nuevoIdEstado;
          }
        },
        error: (error) => {
          console.error('Error al actualizar estado:', error);
          // Mostrar mensaje de error al usuario
        }
      });
    }
  }

  cerrarModalEstado(): void {
    this.mostrarModalEstado = false;
    this.articuloSeleccionado = null;
  }

  cambiarFiltro(filtro: string): void {
    this.filtroSeleccionado = filtro;
  }
}