import { Component, OnInit, output, input } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../service/local/auth.service';
import { Card } from '../../../entities/Card';
import { CardService } from '../../../service/api/card.service';

@Component({
  selector: 'app-registrar-tarjeta',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registrar-tarjeta.component.html',
  styleUrls: ['./registrar-tarjeta.component.scss']
})
export class RegistrarTarjetaComponent implements OnInit {
  idUsuario = input<number>();
  tarjetaRegistrada = output<Card>();
  cancelar = output<void>();

  tarjetaForm!: FormGroup;
  loading = false;
  mensaje = '';
  tipoMensaje: 'success' | 'error' = 'success';
  mostrarCVV = false;

  constructor(private fb: FormBuilder, private cardService: CardService, private authService: AuthService) {}

  ngOnInit(): void {
    this.tarjetaForm = this.fb.group({
      numero: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]],
      fechaVencimiento: ['', [Validators.required, this.validarFechaVencimiento]],
      saldo: [0, [Validators.required, Validators.min(0)]]
    });
  }

  validarFechaVencimiento(control: any) {
    if (!control.value) return null;
    const fecha = new Date(control.value);
    return fecha < new Date() ? { fechaVencida: true } : null;
  }

  registrarTarjeta(): void {
    if (this.tarjetaForm.invalid) {
      this.mostrarMensaje('Complete todos los campos correctamente', 'error');
      Object.keys(this.tarjetaForm.controls).forEach(key => this.tarjetaForm.get(key)?.markAsTouched());
      return;
    }

    const userId = this.idUsuario() ?? Number(this.authService.currentUserValue?.id_usuario);
    
    if (!userId) {
      this.mostrarMensaje('Error: Usuario no autenticado', 'error');
      return;
    }

    this.loading = true;
    const formValue = this.tarjetaForm.value;

    this.cardService.registrarTarjeta(Card.crearDesdeDatos(
      formValue.numero,
      formValue.cvv,
      new Date(formValue.fechaVencimiento),
      parseFloat(formValue.saldo),
      userId
    )).subscribe({
      next: (card) => {
        this.mostrarMensaje('Tarjeta registrada exitosamente', 'success');
        this.tarjetaForm.reset({ saldo: 0 });
        this.loading = false;
        this.tarjetaRegistrada.emit(card);
      },
      error: (error) => {
        console.error('Error al registrar tarjeta:', error);
        this.mostrarMensaje('Error al registrar. Verifique que el número no esté duplicado', 'error');
        this.loading = false;
      }
    });
  }

  mostrarMensaje(mensaje: string, tipo: 'success' | 'error'): void {
    this.mensaje = mensaje;
    this.tipoMensaje = tipo;
    setTimeout(() => this.mensaje = '', 4000);
  }

  formatearNumero(event: any): void {
    let valor = event.target.value.replace(/\D/g, '').substring(0, 16);
    event.target.value = valor;
    this.tarjetaForm.patchValue({ numero: valor });
  }

  formatearCVV(event: any): void {
    let valor = event.target.value.replace(/\D/g, '').substring(0, 4);
    event.target.value = valor;
    this.tarjetaForm.patchValue({ cvv: valor });
  }

  toggleMostrarCVV(): void { this.mostrarCVV = !this.mostrarCVV; }
  onCancelar(): void { this.cancelar.emit(); }

  private esInvalido(campo: string): boolean {
    const control = this.tarjetaForm.get(campo);
    return !!(control?.invalid && control.touched);
  }

  get numeroInvalido(): boolean { return this.esInvalido('numero'); }
  get cvvInvalido(): boolean { return this.esInvalido('cvv'); }
  get fechaInvalida(): boolean { return this.esInvalido('fechaVencimiento'); }
  get saldoInvalido(): boolean { return this.esInvalido('saldo'); }
}