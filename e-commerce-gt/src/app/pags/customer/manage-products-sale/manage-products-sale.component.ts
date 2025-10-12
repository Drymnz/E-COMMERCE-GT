import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductCardManageComponent } from '../product-card-manage/product-card-manage.component';
import { Articulo } from '../../../entities/Customer';
import { ModalSelectOptionComponent } from '../../general/modal-select-option/modal-select-option.component';
import { NotifyConfirmComponent } from '../../general/notify-confirm/notify-confirm.component';
import { ListConstantService } from '../../../service/list-constant.service';

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

  filtroSeleccionado: string = 'todos';
  cargando: boolean = false;

  mostrarModalEstado: boolean = false;
  articuloSeleccionado: Articulo | null = null;

  mostrarModalEliminar: boolean = false;
  articuloAEliminar: Articulo | null = null;

  constructor(
    private router: Router,
    private constantService: ListConstantService
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
  }

  cargarArticulos(): void {
    this.cargando = true;

    setTimeout(() => {
      this.articulos = [
        new Articulo(
          1,
          'Laptop Dell Inspiron 15',
          'Laptop potente para trabajo y estudio con procesador Intel i7, 16GB RAM, SSD 512GB',
          4500.00,
          'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400',
          5,
          1,
          ['Electrónica', 'Computadoras']
        ),
        new Articulo(
          2,
          'Mouse Logitech MX Master 3',
          'Mouse ergonómico inalámbrico de alta precisión',
          350.00,
          'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400',
          0,
          2,
          ['Accesorios', 'Periféricos']
        ),
        new Articulo(
          3,
          'Teclado Mecánico RGB',
          'Teclado gaming con switches mecánicos y retroiluminación personalizable',
          550.00,
          'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400',
          12,
          1,
          ['Gaming', 'Periféricos']
        )
      ];
      this.cargando = false;
    }, 500);
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
    if (idEstado >= 0 && idEstado < this.estadosArticulo.length) {
      return this.estadosArticulo[idEstado];
    }
    return 'Sin estado';
  }

  irAPublicar(): void {
    this.router.navigate(['/register-article']);
  }

  editarProducto(articulo: Articulo): void {
    this.router.navigate(['/editar-producto', articulo.id_articulo]);
  }

  eliminarProducto(articulo: Articulo): void {
    this.articuloAEliminar = articulo;
    this.mostrarModalEliminar = true;
  }

  confirmarEliminar(confirmado: boolean): void {
    if (confirmado && this.articuloAEliminar) {
      console.log('Eliminando producto:', this.articuloAEliminar);
      this.articulos = this.articulos.filter(a => a.id_articulo !== this.articuloAEliminar!.id_articulo);
      this.cerrarModalEliminar();
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
      console.log('Cambiando estado del producto:', this.articuloSeleccionado, 'a:', nuevoEstado);
      this.articuloSeleccionado.id_estado_articulo = indiceEstado;
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