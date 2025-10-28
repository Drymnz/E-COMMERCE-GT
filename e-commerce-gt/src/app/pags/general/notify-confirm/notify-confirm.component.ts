import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notify-confirm',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notify-confirm.component.html',
  styleUrl: './notify-confirm.component.scss'
})
export class NotifyConfirmComponent {
  @Input() mostrar = false;
  @Input() titulo = 'Confirmar acción';
  @Input() mensaje = '¿Está seguro de que desea realizar esta acción?';
  @Input() mensajeSecundario = '';
  @Input() textoConfirmar = 'Confirmar';
  @Input() textoCancelar = 'Cancelar';
  @Input() tipoAlerta: 'danger' | 'warning' | 'info' = 'danger';
  @Input() iconoAlerta = 'bi-exclamation-triangle-fill';
  @Input() mostrarAdvertencia = true;
  @Input() textoAdvertencia = 'Esta acción no se puede deshacer.';

  @Output() confirmar = new EventEmitter<boolean>();
  @Output() cancelar = new EventEmitter<void>();

  private readonly colores = {
    danger: { header: 'bg-danger', boton: 'btn-danger', advertencia: 'alert-danger' },
    warning: { header: 'bg-warning', boton: 'btn-warning', advertencia: 'alert-warning' },
    info: { header: 'bg-info', boton: 'btn-info', advertencia: 'alert-info' }
  };

  onConfirmar(): void { this.confirmar.emit(true); }
  onCancelar(): void { this.cancelar.emit(); }

  get colorHeader(): string { return this.colores[this.tipoAlerta].header; }
  get colorBoton(): string { return this.colores[this.tipoAlerta].boton; }
  get colorAdvertencia(): string { return this.colores[this.tipoAlerta].advertencia; }
}