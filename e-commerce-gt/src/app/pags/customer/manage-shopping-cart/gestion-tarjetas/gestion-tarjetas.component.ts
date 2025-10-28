import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Card } from '../../../../entities/Card';
import { CardService } from '../../../../service/api/card.service';
import { AuthService } from '../../../../service/local/auth.service';
import { NotifyConfirmComponent } from '../../../general/notify-confirm/notify-confirm.component';

@Component({
  selector: 'app-gestion-tarjetas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NotifyConfirmComponent],
  templateUrl: './gestion-tarjetas.component.html',
  styleUrl: './gestion-tarjetas.component.scss'
})
export class GestionTarjetasComponent implements OnInit {
  tarjetas: Card[] = [];
  cargando = true;
  error = false;
  mensajeError = '';
  mostrarFormulario = false;
  tarjetaEditando: Card | null = null;
  nuevaTarjetaForm: FormGroup;
  editarTarjetaForm: FormGroup;

  // Para los modales de confirmación y notificación
  mostrarConfirmEliminar = false;
  tarjetaAEliminar: string | null = null;
  
  // Notificación general
  mostrarNotificacion = false;
  notificacionTitulo = '';
  notificacionMensaje = '';
  notificacionMensajeSecundario = '';
  notificacionTipo: 'danger' | 'warning' | 'info' = 'info';
  notificacionIcono = 'bi-info-circle-fill';

  constructor(private cardService: CardService, private authService: AuthService, private fb: FormBuilder) {
    this.nuevaTarjetaForm = this.fb.group({
      numero: ['', [Validators.required, Validators.minLength(16), Validators.maxLength(16), Validators.pattern(/^\d+$/)]],
      cvv: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(3), Validators.pattern(/^\d+$/)]],
      fecha_vencimiento: ['', Validators.required],
      saldo: [0, [Validators.required, Validators.min(0)]]
    });
    this.editarTarjetaForm = this.fb.group({
      cvv: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(3), Validators.pattern(/^\d+$/)]],
      fecha_vencimiento: ['', Validators.required],
      saldo: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.cargarTarjetas();
  }

  cargarTarjetas(): void {
    const usuario = this.authService.currentUserValue;
    if (!usuario) {
      this.error = true;
      this.mensajeError = 'Debe iniciar sesión para ver sus tarjetas';
      this.cargando = false;
      return;
    }
    this.cargando = true;
    this.error = false;
    this.cardService.obtenerTarjetasUsuario(usuario.id_usuario).subscribe({
      next: (data) => {
        this.tarjetas = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar tarjetas:', err);
        this.error = true;
        this.mensajeError = 'Error al cargar las tarjetas';
        this.cargando = false;
      }
    });
  }

  toggleFormulario(): void {
    this.mostrarFormulario = !this.mostrarFormulario;
    if (!this.mostrarFormulario) {
      this.nuevaTarjetaForm.reset({ saldo: 0 });
    }
  }

  agregarTarjeta(): void {
    if (this.nuevaTarjetaForm.invalid) {
      Object.keys(this.nuevaTarjetaForm.controls).forEach(key => {
        this.nuevaTarjetaForm.get(key)?.markAsTouched();
      });
      return;
    }
    const usuario = this.authService.currentUserValue;
    if (!usuario) return;
    const formValue = this.nuevaTarjetaForm.value;
    const tarjeta = Card.crearDesdeDatos(formValue.numero, formValue.cvv, new Date(formValue.fecha_vencimiento), formValue.saldo, usuario.id_usuario);
    this.cardService.registrarTarjeta(tarjeta).subscribe({
      next: (data) => {
        this.tarjetas.push(data);
        this.toggleFormulario();
        this.nuevaTarjetaForm.reset({ saldo: 0 });
        this.mostrarMensaje('Tarjeta Agregada', 'La tarjeta se ha registrado exitosamente', '', 'info', 'bi-check-circle-fill');
      },
      error: (err) => {
        console.error('Error al agregar tarjeta:', err);
        this.mostrarMensaje('Error al Agregar', 'No se pudo agregar la tarjeta', 'Por favor, intente nuevamente', 'danger', 'bi-x-circle-fill');
      }
    });
  }

  iniciarEdicion(tarjeta: Card): void {
    this.tarjetaEditando = tarjeta;
    this.editarTarjetaForm.patchValue({
      cvv: tarjeta.cvv,
      fecha_vencimiento: tarjeta.fecha_vencimiento.toISOString().split('T')[0],
      saldo: tarjeta.saldo
    });
  }

  cancelarEdicion(): void {
    this.tarjetaEditando = null;
    this.editarTarjetaForm.reset();
  }

  guardarEdicion(): void {
    if (this.editarTarjetaForm.invalid) {
      Object.keys(this.editarTarjetaForm.controls).forEach(key => {
        this.editarTarjetaForm.get(key)?.markAsTouched();
      });
      return;
    }
    const tarjeta = this.tarjetaEditando;
    if (!tarjeta) return;
    const formValue = this.editarTarjetaForm.value;
    tarjeta.cvv = formValue.cvv;
    tarjeta.fecha_vencimiento = new Date(formValue.fecha_vencimiento);
    tarjeta.saldo = formValue.saldo;
    this.cancelarEdicion();
    this.mostrarMensaje('Tarjeta Actualizada', 'Los cambios se guardaron localmente', 'Recuerde implementar el endpoint de actualización', 'warning', 'bi-exclamation-triangle-fill');
  }

  solicitarEliminarTarjeta(numero: string): void {
    this.tarjetaAEliminar = numero;
    this.mostrarConfirmEliminar = true;
  }

  confirmarEliminar(confirmado: boolean): void {
    this.mostrarConfirmEliminar = false;
    if (confirmado && this.tarjetaAEliminar) {
      this.eliminarTarjeta(this.tarjetaAEliminar);
    }
    this.tarjetaAEliminar = null;
  }

  cancelarEliminar(): void {
    this.mostrarConfirmEliminar = false;
    this.tarjetaAEliminar = null;
  }

  eliminarTarjeta(numero: string): void {
    this.cardService.eliminarTarjeta(numero).subscribe({
      next: () => {
        this.tarjetas = this.tarjetas.filter(t => t.numero !== numero);
        this.mostrarMensaje('Tarjeta Eliminada', 'La tarjeta se ha eliminado exitosamente', '', 'info', 'bi-check-circle-fill');
      },
      error: (err) => {
        console.error('Error al eliminar tarjeta:', err);
        this.mostrarMensaje('Error al Eliminar', 'No se pudo eliminar la tarjeta', 'Por favor, intente nuevamente', 'danger', 'bi-x-circle-fill');
      }
    });
  }

  getClaseTarjeta(tarjeta: Card): string {
    if (tarjeta.estaVencida) {
      return 'border-danger';
    }
    return tarjeta.saldo > 0 ? 'border-success' : 'border-warning';
  }

  // Método unificado para mostrar mensajes
  mostrarMensaje(titulo: string, mensaje: string, mensajeSecundario: string, tipo: 'danger' | 'warning' | 'info', icono: string): void {
    this.notificacionTitulo = titulo;
    this.notificacionMensaje = mensaje;
    this.notificacionMensajeSecundario = mensajeSecundario;
    this.notificacionTipo = tipo;
    this.notificacionIcono = icono;
    this.mostrarNotificacion = true;
  }

  cerrarNotificacion(): void {
    this.mostrarNotificacion = false;
    this.notificacionMensajeSecundario = '';
  }
}