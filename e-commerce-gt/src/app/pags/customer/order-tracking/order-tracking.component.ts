import { Component, OnInit } from '@angular/core';
import {  PedidoService } from '../../../service/api/pedido.service';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { AuthService } from '../../../service/local/auth.service';
import { Pedido } from '../../../entities/Pedido';

@Component({
  selector: 'app-order-tracking',
  imports: [],
  templateUrl: './order-tracking.component.html',
  styleUrl: './order-tracking.component.scss'
})
export class OrderTrackingComponent implements OnInit {
  pedidos: Pedido[] = [];
  estadosPedido: string[] = [];
  cargando: boolean = false;
  mensaje: string = '';

  constructor(
    private pedidoService: PedidoService,
    private listConstantService: ListConstantService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarEstadosPedido();
    this.cargarPedidosUsuario();
  }

  cargarEstadosPedido(): void {
    this.listConstantService.estadosPedido$.subscribe(estados => {
      this.estadosPedido = estados;
    });
  }

  cargarPedidosUsuario(): void {
    const usuario = this.authService.currentUserValue;
    
    if (!usuario) {
      this.mensaje = 'No se ha iniciado sesión';
      return;
    }

    this.cargando = true;
    this.pedidoService.obtenerPedidosUsuario(usuario.id_usuario).subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos;
        this.cargando = false;
      },
      error: (error) => {
        this.mensaje = 'Error al cargar pedidos';
        this.cargando = false;
      }
    });
  }

  obtenerNombreEstado(idEstado: number): string {
    return this.estadosPedido[idEstado - 1] || 'Desconocido';
  }

  obtenerClaseEstado(idEstado: number): string {
    return idEstado === 1 ? 'bg-warning' : 'bg-success';
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  calcularDiasRestantes(fecha: string): number {
    const fechaEntrega = new Date(fecha);
    const hoy = new Date();
    const diferencia = fechaEntrega.getTime() - hoy.getTime();
    return Math.ceil(diferencia / (1000 * 60 * 60 * 24));
  }

  obtenerMensajeTiempo(fecha: string): string {
    const dias = this.calcularDiasRestantes(fecha);
    
    if (dias < 0) {
      return `Entrega retrasada por ${Math.abs(dias)} día(s)`;
    } else if (dias === 0) {
      return 'Entrega hoy';
    } else if (dias === 1) {
      return 'Entrega mañana';
    } else {
      return `Faltan ${dias} día(s)`;
    }
  }
}