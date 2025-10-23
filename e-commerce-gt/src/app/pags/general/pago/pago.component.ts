import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RegistrarTarjetaComponent } from '../registrar-tarjeta/registrar-tarjeta.component';
import { AuthService } from '../../../service/local/auth.service';
import { Usuario } from '../../../entities/Usuario';
import { Card } from '../../../entities/Card';
import { CarritoService } from '../../../service/local/carrito.service';
import { CardService } from '../../../service/api/card.service';
import { CompraService } from '../../../service/api/compra.service';
import { ItemCarrito } from '../../../entities/ItemCarrito';
import { NotifyConfirmComponent } from "../notify-confirm/notify-confirm.component";
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-pago',
  standalone: true,
  imports: [CommonModule, RegistrarTarjetaComponent, NotifyConfirmComponent],
  templateUrl: './pago.component.html',
  styleUrls: ['./pago.component.scss']
})
export class PagoComponent implements OnInit {
  usuarioActual = signal<Usuario | null>(null);
  idUsuario = computed(() => this.usuarioActual()?.id_usuario ?? 0);

  tarjetasUsuario = signal<Card[]>([]);
  tarjetaSeleccionada = signal<Card | null>(null);
  mostrarRegistroTarjeta = signal(false);
  loading = signal(false);
  loadingTarjetas = signal(true);
  
  itemsCarrito = computed(() => this.carritoService.items());
  totalCompra = computed(() => this.carritoService.totalPrecio());
  
  mensaje = signal('');
  tipoMensaje = signal<'success' | 'error' | 'warning'>('success');
  
  pasoActual = signal(1);

  mostrarConfirmacionCancelacion = signal(false);
  mostrarConfirmacionPago = signal(false);
  mostrarConfirmacionEliminar = signal(false);
  tarjetaAEliminar = signal<Card | null>(null);
  eliminandoTarjeta = signal(false);

  constructor(
    public carritoService: CarritoService,
    private cardService: CardService,
    private compraService: CompraService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.verificarAutenticacion();
    this.verificarCarrito();
    this.cargarTarjetasUsuario();
  }

  verificarAutenticacion(): void {
    const usuario = this.authService.currentUserValue;
    
    if (!usuario) {
      this.mostrarMensaje('Debe iniciar sesión para continuar con la compra', 'error');
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }
    
    this.usuarioActual.set(usuario);
  }

  verificarCarrito(): void {
    if (this.itemsCarrito().length === 0) {
      this.mostrarMensaje('El carrito está vacío. Redirigiendo...', 'warning');
      setTimeout(() => {
        this.router.navigate(['/carrito']);
      }, 2000);
    }
  }

  cargarTarjetasUsuario(): void {
    const userId = this.idUsuario();
    
    if (!userId) {
      this.mostrarMensaje('Error: No se pudo obtener la información del usuario', 'error');
      this.loadingTarjetas.set(false);
      return;
    }

    this.loadingTarjetas.set(true);
    this.cardService.obtenerTarjetasUsuario(userId).subscribe({
      next: (tarjetas) => {
        const tarjetasValidas = tarjetas.filter(t => !t.estaVencida);
        this.tarjetasUsuario.set(tarjetasValidas);
        this.loadingTarjetas.set(false);
        
        if (tarjetasValidas.length === 0) {
          this.mostrarRegistroTarjeta.set(true);
          this.mostrarMensaje('No tienes tarjetas válidas. Por favor registra una nueva', 'warning');
        }
      },
      error: (error) => {
        console.error('Error al cargar tarjetas:', error);
        this.loadingTarjetas.set(false);
        
        const mensajeError = error?.error?.message || error?.message || 'Error desconocido';
        this.mostrarMensaje(
          `No se pudieron cargar las tarjetas: ${mensajeError}. Por favor, intente nuevamente.`,
          'error'
        );
      }
    });
  }

  seleccionarTarjeta(tarjeta: Card): void {
    this.tarjetaSeleccionada.set(tarjeta);
    this.mostrarMensaje('Tarjeta seleccionada correctamente', 'success');
  }

  onTarjetaRegistrada(tarjeta: Card): void {
    this.tarjetasUsuario.update(tarjetas => [...tarjetas, tarjeta]);
    this.tarjetaSeleccionada.set(tarjeta);
    this.mostrarRegistroTarjeta.set(false);
    this.mostrarMensaje('Tarjeta registrada y seleccionada exitosamente', 'success');
  }

  toggleRegistroTarjeta(): void {
    this.mostrarRegistroTarjeta.update(v => !v);
    if (this.mostrarRegistroTarjeta()) {
      this.tarjetaSeleccionada.set(null);
    }
  }

  avanzarPaso(): void {
    const paso = this.pasoActual();
    if (paso === 1) {
      this.pasoActual.set(2);
    } else if (paso === 2) {
      if (!this.tarjetaSeleccionada()) {
        this.mostrarMensaje('Debe seleccionar o registrar una tarjeta para continuar', 'warning');
        return;
      }
      this.pasoActual.set(3);
    }
  }

  retrocederPaso(): void {
    if (this.pasoActual() > 1) {
      this.pasoActual.update(v => v - 1);
    }
  }

  calcularSubtotal(item: ItemCarrito): number {
    return item.articulo.precio * item.cantidad;
  }

  abrirConfirmacionPago(): void {
    const tarjeta = this.tarjetaSeleccionada();
    if (!tarjeta) {
      this.mostrarMensaje('Debe seleccionar una tarjeta', 'warning');
      return;
    }

    if (!tarjeta.tieneSaldoSuficiente(this.totalCompra())) {
      this.mostrarMensaje(
        `Saldo insuficiente. Necesita Q ${this.totalCompra().toFixed(2)} pero solo tiene Q ${tarjeta.saldo.toFixed(2)}`,
        'error'
      );
      return;
    }

    this.mostrarConfirmacionPago.set(true);
  }

  private agruparPorVendedor(): Map<number, ItemCarrito[]> {
    const grupos = new Map<number, ItemCarrito[]>();
    
    this.itemsCarrito().forEach(item => {
      const idVendedor = item.articulo.id_vendedor || 0;
      
      if (!grupos.has(idVendedor)) {
        grupos.set(idVendedor, []);
      }
      
      grupos.get(idVendedor)!.push(item);
    });
    
    return grupos;
  }

  private calcularTotalGrupo(items: ItemCarrito[]): number {
    return items.reduce((total, item) => total + (item.articulo.precio * item.cantidad), 0);
  }

  procesarPago(): void {
    const tarjeta = this.tarjetaSeleccionada();
    if (!tarjeta) return;

    this.loading.set(true);
    this.mostrarConfirmacionPago.set(false);

    const gruposPorVendedor = this.agruparPorVendedor();

    const compras$ = Array.from(gruposPorVendedor.entries()).map(([idVendedor, items]) => {
      const carritoCompra = {
        id_usuario: this.idUsuario(),
        id_vendedor: idVendedor,
        items: items.map(item => ({
          articulo: {
            id_articulo: item.articulo.id_articulo,
            nombre: item.articulo.nombre,
            precio: item.articulo.precio,
            stock: item.articulo.stock
          },
          cantidad: item.cantidad
        }))
      };
      
      return this.compraService.procesarCompra(carritoCompra);
    });

    forkJoin(compras$).subscribe({
      next: (respuestas) => {
        const todasExitosas = respuestas.every(r => r.exitoso);
        
        if (todasExitosas) {
          this.cardService.reducirSaldo(tarjeta.numero, this.totalCompra()).subscribe({
            next: (respuestaPago) => {
              if (respuestaPago.success) {
                const pedidos = respuestas
                  .map((r, i) => `Pedido #${r.id_pedido}`)
                  .join(', ');
                
                this.mostrarMensaje(
                  `¡Compra realizada exitosamente! ${pedidos}. Total cobrado: Q ${this.totalCompra().toFixed(2)}`, 
                  'success'
                );
                
                this.carritoService.vaciarCarrito();
                
                setTimeout(() => {
                  this.router.navigate(['/']);
                }, 4000);
              } else {
                this.mostrarMensaje(
                  `Error en el pago: ${respuestaPago.message}. Las compras fueron registradas pero no se pudo cobrar.`, 
                  'error'
                );
                this.loading.set(false);
              }
            },
            error: (errorPago) => {
              console.error('Error al procesar pago:', errorPago);
              this.loading.set(false);
              
              const mensajeError = errorPago?.error?.message || errorPago?.message || 'Error desconocido';
              const pedidos = respuestas.map(r => `#${r.id_pedido}`).join(', ');
              
              this.mostrarMensaje(
                `Las compras fueron registradas (${pedidos}) pero hubo un error al cobrar: ${mensajeError}. Por favor contacte con soporte.`,
                'error'
              );
            }
          });
        } else {
          this.loading.set(false);
          
          const fallidas = respuestas
            .filter(r => !r.exitoso)
            .map(r => r.mensaje)
            .join('; ');
          
          this.mostrarMensaje(
            `No se pudieron procesar todas las compras: ${fallidas}`,
            'error'
          );
        }
      },
      error: (errorCompra) => {
        console.error('Error al procesar compras:', errorCompra);
        this.loading.set(false);
        
        const mensajeError = errorCompra?.error?.mensaje || 
                            errorCompra?.error?.message || 
                            errorCompra?.message || 
                            'Error desconocido';
        this.mostrarMensaje(
          `Error al procesar las compras: ${mensajeError}. Por favor, intente nuevamente.`,
          'error'
        );
      }
    });
  }

  abrirConfirmacionCancelacion(): void {
    this.mostrarConfirmacionCancelacion.set(true);
  }

  cancelarCompra(): void {
    this.mostrarConfirmacionCancelacion.set(false);
    this.mostrarMensaje('Compra cancelada. Redirigiendo...', 'warning');
    setTimeout(() => {
      this.router.navigate(['/']);
    }, 1500);
  }

  cerrarConfirmacionCancelacion(): void {
    this.mostrarConfirmacionCancelacion.set(false);
  }

  cerrarConfirmacionPago(): void {
    this.mostrarConfirmacionPago.set(false);
  }

  abrirConfirmacionEliminar(tarjeta: Card): void {
    this.tarjetaAEliminar.set(tarjeta);
    this.mostrarConfirmacionEliminar.set(true);
  }

  eliminarTarjeta(): void {
    const tarjeta = this.tarjetaAEliminar();
    if (!tarjeta) return;

    this.eliminandoTarjeta.set(true);

    this.cardService.eliminarTarjeta(tarjeta.numero).subscribe({
      next: () => {
        this.tarjetasUsuario.update(tarjetas => 
          tarjetas.filter(t => t.numero !== tarjeta.numero)
        );

        if (this.tarjetaSeleccionada()?.numero === tarjeta.numero) {
          this.tarjetaSeleccionada.set(null);
        }

        this.mostrarMensaje('Tarjeta eliminada exitosamente', 'success');
        this.cerrarConfirmacionEliminar();
        this.eliminandoTarjeta.set(false);

        if (this.tarjetasUsuario().length === 0) {
          this.mostrarRegistroTarjeta.set(true);
        }
      },
      error: (error) => {
        console.error('Error al eliminar tarjeta:', error);
        this.eliminandoTarjeta.set(false);
        
        const mensajeError = error?.error?.message || error?.message || 'Error desconocido';
        this.mostrarMensaje(
          `No se pudo eliminar la tarjeta: ${mensajeError}. Por favor, intente nuevamente.`,
          'error'
        );
        this.cerrarConfirmacionEliminar();
      }
    });
  }

  cerrarConfirmacionEliminar(): void {
    this.mostrarConfirmacionEliminar.set(false);
    this.tarjetaAEliminar.set(null);
  }

  mostrarMensaje(mensaje: string, tipo: 'success' | 'error' | 'warning'): void {
    this.mensaje.set(mensaje);
    this.tipoMensaje.set(tipo);
    setTimeout(() => this.mensaje.set(''), 6000);
  }

  get nombreUsuario(): string {
    return this.usuarioActual()?.nombreCompleto ?? 'Usuario';
  }
}