import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArticleComponent } from '../../general/article/article.component';
import { NotifyConfirmComponent } from '../../general/notify-confirm/notify-confirm.component';
import { Articulo } from '../../../entities/Article';
import { ModeratorService, PaginacionResponse } from '../../../service/api/moderacion.service';
import { ListConstantService } from '../../../service/api/list-constant.service';

@Component({
  selector: 'app-panel-moderacion',
  standalone: true,
  imports: [CommonModule, ArticleComponent, NotifyConfirmComponent],
  templateUrl: './panel-moderacion.component.html',
  styleUrls: ['./panel-moderacion.component.scss']
})
export class PanelModeracionComponent implements OnInit {
  articulos: Articulo[] = [];
  estadosArticulo: string[] = [];
  estadosModeracion: string[] = [];
  
  // Paginación
  paginaActual: number = 1;
  tamanoPagina: number = 5;
  totalPaginas: number = 0;
  totalArticulos: number = 0;
  
  cargando: boolean = false;

  // Control de modales
  mostrarModalAprobar: boolean = false;
  mostrarModalRechazar: boolean = false;
  articuloSeleccionado: Articulo | null = null;

  constructor(
    private moderatorService: ModeratorService,
    private constantService: ListConstantService
  ) {}

  ngOnInit(): void {
    this.cargarConstantes();
    this.cargarArticulosPendientes();
  }

  cargarConstantes(): void {
    this.constantService.estadosArticulo$.subscribe(estados => {
      this.estadosArticulo = estados;
    });

    this.constantService.estadosModeracion$.subscribe(estados => {
      this.estadosModeracion = estados;
    });
  }

  cargarArticulosPendientes(): void {
    this.cargando = true;
    this.moderatorService.obtenerArticulosPendientes(this.paginaActual, this.tamanoPagina)
      .subscribe({
        next: (response: PaginacionResponse) => {
          this.articulos = response.articulos.map(a => Articulo.fromJSON(a));
          this.totalPaginas = response.totalPaginas;
          this.totalArticulos = response.totalArticulos;
          this.paginaActual = response.paginaActual;
          this.cargando = false;
        },
        error: (error) => {
          console.error('Error al cargar artículos pendientes:', error);
          this.cargando = false;
        }
      });
  }

  // Abrir modal de aprobación
  abrirModalAprobar(articulo: Articulo): void {
    this.articuloSeleccionado = articulo;
    this.mostrarModalAprobar = true;
  }

  // Confirmar aprobación
  confirmarAprobar(): void {
    if (this.articuloSeleccionado) {
      this.moderatorService.aprobarArticulo(this.articuloSeleccionado.id_articulo)
        .subscribe({
          next: (response) => {
            console.log('Artículo aprobado:', response);
            this.cargarArticulosPendientes();
            this.cerrarModalAprobar();
          },
          error: (error) => {
            console.error('Error al aprobar:', error);
            this.cerrarModalAprobar();
          }
        });
    }
  }

  // Cerrar modal de aprobación
  cerrarModalAprobar(): void {
    this.mostrarModalAprobar = false;
    this.articuloSeleccionado = null;
  }

  // Abrir modal de rechazo
  abrirModalRechazar(articulo: Articulo): void {
    this.articuloSeleccionado = articulo;
    this.mostrarModalRechazar = true;
  }

  // Confirmar rechazo
  confirmarRechazar(): void {
    if (this.articuloSeleccionado) {
      this.moderatorService.rechazarArticulo(this.articuloSeleccionado.id_articulo)
        .subscribe({
          next: (response) => {
            console.log('Artículo rechazado:', response);
            this.cargarArticulosPendientes();
            this.cerrarModalRechazar();
          },
          error: (error) => {
            console.error('Error al rechazar:', error);
            this.cerrarModalRechazar();
          }
        });
    }
  }

  // Cerrar modal de rechazo
  cerrarModalRechazar(): void {
    this.mostrarModalRechazar = false;
    this.articuloSeleccionado = null;
  }

  // Métodos de paginación
  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
      this.cargarArticulosPendientes();
    }
  }

  paginaAnterior(): void {
    this.cambiarPagina(this.paginaActual - 1);
  }

  paginaSiguiente(): void {
    this.cambiarPagina(this.paginaActual + 1);
  }

  obtenerEstadoNombre(idEstado: number): string {
    if (idEstado >= 0 && idEstado < this.estadosArticulo.length) {
      return this.estadosArticulo[idEstado];
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

  get rangoPaginas(): number[] {
    const rango = [];
    const inicio = Math.max(1, this.paginaActual - 2);
    const fin = Math.min(this.totalPaginas, this.paginaActual + 2);
    
    for (let i = inicio; i <= fin; i++) {
      rango.push(i);
    }
    return rango;
  }
}