import { Component, OnInit, signal } from '@angular/core';
import { Notificacion } from '../../../entities/Notificacion';
import { NotificacionService } from '../../../service/api/notificacion.service';

@Component({
  selector: 'app-notificacion',
  imports: [],
  templateUrl: './notificacion.component.html',
  styleUrl: './notificacion.component.scss'
})
export class NotificacionesComponent implements OnInit {
  // Signals
  notificaciones = signal<Notificacion[]>([]);
  cargando = signal<boolean>(false);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);
  totalNotificaciones = signal<number>(0);
  totalPages = signal<number>(0);

  constructor(private notificacionService: NotificacionService) {}

  ngOnInit(): void {
    this.cargarNotificaciones();
  }

  cargarNotificaciones(): void {
    this.cargando.set(true);
    
    this.notificacionService.obtenerNotificacionesPaginadas(
      this.currentPage(),
      this.pageSize()
    ).subscribe({
      next: (response) => {
        this.notificaciones.set(response.notificaciones);
        this.currentPage.set(response.currentPage);
        this.pageSize.set(response.pageSize);
        this.totalNotificaciones.set(response.totalNotificaciones);
        this.totalPages.set(response.totalPages);
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar notificaciones:', error);
        this.cargando.set(false);
      }
    });
  }

  cambiarPagina(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
      this.cargarNotificaciones();
    }
  }

  get paginaAnteriorDisabled(): boolean {
    return this.currentPage() <= 1;
  }

  get paginaSiguienteDisabled(): boolean {
    return this.currentPage() >= this.totalPages();
  }

  get rangoNotificaciones(): string {
    if (this.totalNotificaciones() === 0) return '0-0';
    const inicio = (this.currentPage() - 1) * this.pageSize() + 1;
    const fin = Math.min(this.currentPage() * this.pageSize(), this.totalNotificaciones());
    return `${inicio}-${fin}`;
  }

  get paginas(): number[] {
    const total = this.totalPages();
    const current = this.currentPage();
    const delta = 2;
    const range: number[] = [];
    
    for (let i = Math.max(2, current - delta); i <= Math.min(total - 1, current + delta); i++) {
      range.push(i);
    }

    if (current - delta > 2) {
      range.unshift(-1);
    }
    if (current + delta < total - 1) {
      range.push(-1);
    }

    range.unshift(1);
    if (total > 1) {
      range.push(total);
    }

    return range;
  }
}