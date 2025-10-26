import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Articulo } from '../../../entities/Article';
import { Usuario } from '../../../entities/Usuario';
import { ReportService } from '../../../service/api/report.service';

interface FiltroFechas {
  fechaInicio: string;
  fechaFin: string;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {
  productosVendidos = signal<Articulo[]>([]);
  clientesCompradores = signal<Usuario[]>([]);
  clientesVendedores = signal<Usuario[]>([]);
  clientesPedidos = signal<Usuario[]>([]);
  clientesProductosVenta = signal<Usuario[]>([]);

  cargandoProductos = signal<boolean>(false);
  cargandoCompradores = signal<boolean>(false);
  cargandoVendedores = signal<boolean>(false);
  cargandoPedidos = signal<boolean>(false);
  cargandoProductosVenta = signal<boolean>(false);

  filtroProductos: FiltroFechas = this.inicializarFechas();
  filtroCompradores: FiltroFechas = this.inicializarFechas();
  filtroVendedores: FiltroFechas = this.inicializarFechas();
  filtroPedidos: FiltroFechas = this.inicializarFechas();

  pestanaActiva = signal<string>('productos');

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.cargarReporteProductos();
  }

  private inicializarFechas(): FiltroFechas {
    const fechaFin = new Date();
    const fechaInicio = new Date();
    fechaInicio.setMonth(fechaInicio.getMonth() - 1);

    return {
      fechaInicio: this.formatearFechaInput(fechaInicio),
      fechaFin: this.formatearFechaInput(fechaFin)
    };
  }

  private formatearFechaInput(fecha: Date): string {
    return fecha.toISOString().slice(0, 16); // YYYY-MM-DDTHH:mm
  }

  private formatearFechaAPI(fechaInput: string): string {
    return fechaInput + ':00'; // YYYY-MM-DDTHH:mm:ss
  }

  cambiarPestana(pestana: string): void {
    this.pestanaActiva.set(pestana);
    
    // Cargar datos si aún no se han cargado
    switch(pestana) {
      case 'productos':
        if (this.productosVendidos().length === 0) this.cargarReporteProductos();
        break;
      case 'compradores':
        if (this.clientesCompradores().length === 0) this.cargarReporteCompradores();
        break;
      case 'vendedores':
        if (this.clientesVendedores().length === 0) this.cargarReporteVendedores();
        break;
      case 'pedidos':
        if (this.clientesPedidos().length === 0) this.cargarReportePedidos();
        break;
      case 'productos-venta':
        if (this.clientesProductosVenta().length === 0) this.cargarReporteProductosVenta();
        break;
    }
  }

  cargarReporteProductos(): void {
    this.cargandoProductos.set(true);
    const fechaInicio = this.formatearFechaAPI(this.filtroProductos.fechaInicio);
    const fechaFin = this.formatearFechaAPI(this.filtroProductos.fechaFin);

    this.reportService.obtenerTopProductosVendidos(fechaInicio, fechaFin).subscribe({
      next: (productos) => {
        this.productosVendidos.set(productos);
        this.cargandoProductos.set(false);
      },
      error: (error) => {
        console.error('Error al cargar productos vendidos:', error);
        this.cargandoProductos.set(false);
      }
    });
  }

  cargarReporteCompradores(): void {
    this.cargandoCompradores.set(true);
    const fechaInicio = this.formatearFechaAPI(this.filtroCompradores.fechaInicio);
    const fechaFin = this.formatearFechaAPI(this.filtroCompradores.fechaFin);

    this.reportService.obtenerTopClientesCompradores(fechaInicio, fechaFin).subscribe({
      next: (clientes) => {
        this.clientesCompradores.set(clientes);
        this.cargandoCompradores.set(false);
      },
      error: (error) => {
        console.error('Error al cargar clientes compradores:', error);
        this.cargandoCompradores.set(false);
      }
    });
  }

  cargarReporteVendedores(): void {
    this.cargandoVendedores.set(true);
    const fechaInicio = this.formatearFechaAPI(this.filtroVendedores.fechaInicio);
    const fechaFin = this.formatearFechaAPI(this.filtroVendedores.fechaFin);

    this.reportService.obtenerTopClientesVendedores(fechaInicio, fechaFin).subscribe({
      next: (clientes) => {
        this.clientesVendedores.set(clientes);
        this.cargandoVendedores.set(false);
      },
      error: (error) => {
        console.error('Error al cargar clientes vendedores:', error);
        this.cargandoVendedores.set(false);
      }
    });
  }

  cargarReportePedidos(): void {
    this.cargandoPedidos.set(true);
    const fechaInicio = this.formatearFechaAPI(this.filtroPedidos.fechaInicio);
    const fechaFin = this.formatearFechaAPI(this.filtroPedidos.fechaFin);

    this.reportService.obtenerTopClientesPedidos(fechaInicio, fechaFin).subscribe({
      next: (clientes) => {
        this.clientesPedidos.set(clientes);
        this.cargandoPedidos.set(false);
      },
      error: (error) => {
        console.error('Error al cargar clientes con pedidos:', error);
        this.cargandoPedidos.set(false);
      }
    });
  }

  cargarReporteProductosVenta(): void {
    this.cargandoProductosVenta.set(true);

    this.reportService.obtenerTopClientesProductosVenta().subscribe({
      next: (clientes) => {
        this.clientesProductosVenta.set(clientes);
        this.cargandoProductosVenta.set(false);
      },
      error: (error) => {
        console.error('Error al cargar clientes con productos en venta:', error);
        this.cargandoProductosVenta.set(false);
      }
    });
  }

  aplicarRangoRapido(rango: 'semana' | 'mes' | 'anio', filtro: FiltroFechas): void {
    const fechaFin = new Date();
    const fechaInicio = new Date();

    switch(rango) {
      case 'semana':
        fechaInicio.setDate(fechaInicio.getDate() - 7);
        break;
      case 'mes':
        fechaInicio.setMonth(fechaInicio.getMonth() - 1);
        break;
      case 'anio':
        fechaInicio.setFullYear(fechaInicio.getFullYear() - 1);
        break;
    }

    filtro.fechaInicio = this.formatearFechaInput(fechaInicio);
    filtro.fechaFin = this.formatearFechaInput(fechaFin);
  }
}