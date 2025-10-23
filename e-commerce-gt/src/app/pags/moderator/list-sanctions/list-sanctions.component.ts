import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Sancion } from '../../../entities/Sancion';
import { ModeratorService } from '../../../service/api/moderacion.service';
import { SancionResponse } from '../../../entities/SancionResponse';

@Component({
  selector: 'app-list-sanctions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './list-sanctions.component.html',
  styleUrl: './list-sanctions.component.scss'
})
export class ListSanctionsComponent implements OnInit {
  sanciones: Sancion[] = [];
  totalSanciones: number = 0;
  totalPaginas: number = 0;
  paginaActual: number = 1;
  tamanoPagina: number = 10;
  isLoading: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(private moderatorService: ModeratorService) { }

  ngOnInit(): void {
    this.cargarSanciones();
  }

  cargarSanciones(): void {
    this.isLoading = true;
    this.ocultarMensajes();

    this.moderatorService.obtenerSancionesPaginadas(this.paginaActual, this.tamanoPagina)
      .subscribe({
        next: (response: SancionResponse) => {
          console.log('Sanciones recibidas:', response);
          console.log('Sanciones array:', response.sanciones);

          // Mapear a objetos Sancion
          this.sanciones = response.sanciones.map(s => Sancion.fromJSON(s));
          console.log('Sanciones mapeadas:', this.sanciones);
          
          this.totalSanciones = response.totalSanciones;
          this.totalPaginas = response.totalPaginas;
          this.paginaActual = response.paginaActual;
          this.isLoading = false;

          if (this.sanciones.length === 0 && this.paginaActual === 1) {
            this.mensajeError = 'No se encontraron sanciones registradas';
          }
        },
        error: (error) => {
          console.error('Error al cargar sanciones:', error);
          this.mensajeError = 'Error al cargar el historial de sanciones. Por favor, intente nuevamente.';
          this.isLoading = false;
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarSanciones();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  cambiarTamanoPagina(nuevoTamano: number): void {
    if (nuevoTamano !== this.tamanoPagina) {
      this.tamanoPagina = nuevoTamano;
      this.paginaActual = 1;
      this.cargarSanciones();
    }
  }

  obtenerPaginasVisibles(): number[] {
    const paginas: number[] = [];
    const inicio = Math.max(2, this.paginaActual - 1);
    const fin = Math.min(this.totalPaginas - 1, this.paginaActual + 1);

    for (let i = inicio; i <= fin; i++) {
      paginas.push(i);
    }

    return paginas;
  }

  ocultarMensajes(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
  }
}