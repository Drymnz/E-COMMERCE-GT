import { Component, OnInit } from '@angular/core';
import { VentaService, VentaTotal } from '../../../service/api/venta.service';

@Component({
  selector: 'app-ventas',
  imports: [],
  templateUrl: './ventas.component.html',
  styleUrl: './ventas.component.scss'
})
export class VentasComponent implements OnInit {
  ventas: VentaTotal[] = [];
  totalGeneral: number = 0;
  comisionPagina: number = 0;
  gananciaCliente: number = 0;
  cargando: boolean = true;

  constructor(private ventaService: VentaService) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;

    // Cargar listado de ventas
    this.ventaService.obtenerTotalVentas().subscribe({
      next: (data) => {
        this.ventas = data;
      },
      error: (error) => {
        console.error('Error al cargar ventas:', error);
        this.cargando = false;
      }
    });

    // Cargar total general
    this.ventaService.obtenerTotalGeneral().subscribe({
      next: (total) => {
        this.totalGeneral = total;
        this.calcularDistribucion();
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar total general:', error);
        this.cargando = false;
      }
    });
  }

  calcularDistribucion(): void {
    this.comisionPagina = this.totalGeneral * 0.05;
    this.gananciaCliente = this.totalGeneral * 0.95;
  }

  formatearMoneda(valor: number): string {
    return `Q ${valor.toFixed(2)}`;
  }

  calcularComision(total: number): number {
    return total * 0.05;
  }

  calcularGananciaVendedor(total: number): number {
    return total * 0.95;
  }
}