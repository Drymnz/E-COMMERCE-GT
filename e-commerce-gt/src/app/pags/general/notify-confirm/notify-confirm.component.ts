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
  @Input() mostrar: boolean = false;
  @Input() titulo: string = 'Confirmar acción';
  @Input() mensaje: string = '¿Está seguro de que desea realizar esta acción?';
  @Input() mensajeSecundario: string = '';
  @Input() textoConfirmar: string = 'Confirmar';
  @Input() textoCancelar: string = 'Cancelar';
  @Input() tipoAlerta: 'danger' | 'warning' | 'info' = 'danger';
  @Input() iconoAlerta: string = 'bi-exclamation-triangle-fill';
  @Input() mostrarAdvertencia: boolean = true;
  @Input() textoAdvertencia: string = 'Esta acción no se puede deshacer.';

  @Output() confirmar = new EventEmitter<boolean>();
  @Output() cancelar = new EventEmitter<void>();

  onConfirmar(): void {
    this.confirmar.emit(true);
  }

  onCancelar(): void {
    this.cancelar.emit();
  }

  get colorHeader(): string {
    const colores = {
      danger: 'bg-danger',
      warning: 'bg-warning',
      info: 'bg-info'
    };
    return colores[this.tipoAlerta];
  }

  get colorBoton(): string {
    const colores = {
      danger: 'btn-danger',
      warning: 'btn-warning',
      info: 'btn-info'
    };
    return colores[this.tipoAlerta];
  }

  get colorAdvertencia(): string {
    const colores = {
      danger: 'alert-danger',
      warning: 'alert-warning',
      info: 'alert-info'
    };
    return colores[this.tipoAlerta];
  }
}