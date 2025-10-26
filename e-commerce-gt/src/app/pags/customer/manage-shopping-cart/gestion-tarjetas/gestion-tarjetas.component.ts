import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Card } from '../../../../entities/Card';
import { CardService } from '../../../../service/api/card.service';
import { AuthService } from '../../../../service/local/auth.service';

@Component({
  selector: 'app-gestion-tarjetas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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

  constructor(
    private cardService: CardService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
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

    const tarjeta = Card.crearDesdeDatos(
      formValue.numero,
      formValue.cvv,
      new Date(formValue.fecha_vencimiento),
      formValue.saldo,
      usuario.id_usuario
    );

    this.cardService.registrarTarjeta(tarjeta).subscribe({
      next: (data) => {
        this.tarjetas.push(data);
        this.toggleFormulario();
        this.nuevaTarjetaForm.reset({ saldo: 0 });
        alert('Tarjeta agregada exitosamente');
      },
      error: (err) => {
        console.error('Error al agregar tarjeta:', err);
        alert('Error al agregar la tarjeta');
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
    alert('Tarjeta actualizada (solo localmente - implementar endpoint de actualización)');
  }

  eliminarTarjeta(numero: string): void {
    if (!confirm('¿Está seguro de eliminar esta tarjeta?')) {
      return;
    }

    this.cardService.eliminarTarjeta(numero).subscribe({
      next: () => {
        this.tarjetas = this.tarjetas.filter(t => t.numero !== numero);
        alert('Tarjeta eliminada exitosamente');
      },
      error: (err) => {
        console.error('Error al eliminar tarjeta:', err);
        alert('Error al eliminar la tarjeta');
      }
    });
  }

  getClaseTarjeta(tarjeta: Card): string {
    if (tarjeta.estaVencida) {
      return 'border-danger';
    }
    return tarjeta.saldo > 0 ? 'border-success' : 'border-warning';
  }
}