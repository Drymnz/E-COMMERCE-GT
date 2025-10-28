import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pedido } from '../../../entities/Pedido';
import { PedidoService } from '../../../service/api/pedido.service';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { ModalSelectOptionComponent } from '../../general/modal-select-option/modal-select-option.component';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalSelectOptionComponent],
  templateUrl: './order-management.component.html',
  styleUrl: './order-management.component.scss'
})
export class OrderManagementComponent implements OnInit {
  pedidosEnCurso: Pedido[] = [];
  estadosPedido: string[] = [];
  pedidoEditando: number | null = null;
  nuevaFecha = '';
  cargando = false;
  mensaje = '';
  tipoMensaje: 'success' | 'error' = 'success';
  mostrarModalEstado = false;
  pedidoSeleccionado = 0;

  constructor(private pedidoService: PedidoService, private listConstantService: ListConstantService) {}

  ngOnInit(): void {
    this.listConstantService.estadosPedido$.subscribe(estados => this.estadosPedido = estados);
    this.cargarPedidosEnCurso();
  }

  cargarPedidosEnCurso(): void {
    this.cargando = true;
    this.pedidoService.obtenerPedidosEnCurso().subscribe({
      next: (pedidos) => {
        this.pedidosEnCurso = pedidos;
        this.cargando = false;
      },
      error: () => {
        this.mostrarMensaje('Error al cargar pedidos', 'error');
        this.cargando = false;
      }
    });
  }

  iniciarEdicionFecha(idPedido: number, fechaActual: string): void {
    this.pedidoEditando = idPedido;
    this.nuevaFecha = fechaActual.substring(0, 16);
  }

  cancelarEdicion(): void {
    this.pedidoEditando = null;
    this.nuevaFecha = '';
  }

  guardarFecha(idPedido: number): void {
    if (!this.nuevaFecha) {
      this.mostrarMensaje('Debe seleccionar una fecha', 'error');
      return;
    }

    this.pedidoService.actualizarFechaEntrega(idPedido, this.nuevaFecha).subscribe({
      next: () => {
        this.mostrarMensaje('Fecha actualizada correctamente', 'success');
        this.cancelarEdicion();
        this.cargarPedidosEnCurso();
      },
      error: () => this.mostrarMensaje('Error al actualizar fecha', 'error')
    });
  }

  abrirModalEstado(idPedido: number): void {
    this.pedidoSeleccionado = idPedido;
    this.mostrarModalEstado = true;
  }

  cerrarModalEstado(): void {
    this.mostrarModalEstado = false;
    this.pedidoSeleccionado = 0;
  }

  cambiarEstado(estadoSeleccionado: string): void {
    const nuevoIdEstado = this.estadosPedido.indexOf(estadoSeleccionado) + 1;
    
    this.pedidoService.actualizarEstado(this.pedidoSeleccionado, nuevoIdEstado).subscribe({
      next: () => {
        this.mostrarMensaje(`Estado actualizado a: ${estadoSeleccionado}`, 'success');
        this.cerrarModalEstado();
        this.cargarPedidosEnCurso();
      },
      error: () => {
        this.mostrarMensaje('Error al actualizar estado', 'error');
        this.cerrarModalEstado();
      }
    });
  }

  obtenerNombreEstado(idEstado: number): string {
    return this.estadosPedido[idEstado - 1] || 'Desconocido';
  }

  obtenerClaseBadge(idEstado: number): string {
    const clases: { [key: number]: string } = {
      1: 'bg-warning',
      2: 'bg-success',
      3: 'bg-info',
      4: 'bg-primary',
      5: 'bg-danger'
    };
    return clases[idEstado] || 'bg-secondary';
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'error'): void {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
    setTimeout(() => this.mensaje = '', 3000);
  }
}