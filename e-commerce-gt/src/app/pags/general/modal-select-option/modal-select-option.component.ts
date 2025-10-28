import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal-select-option',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-select-option.component.html',
  styleUrls: ['./modal-select-option.component.scss']
})
export class ModalSelectOptionComponent {
  @Input() titulo = 'Seleccionar opción';
  @Input() descripcion = '';
  @Input() opciones: string[] = [];
  @Input() mostrar = false;
  
  @Output() seleccionar = new EventEmitter<string>();
  @Output() cancelar = new EventEmitter<void>();

  opcionSeleccionada = '';

  seleccionarOpcion(opcion: string): void {
    this.opcionSeleccionada = opcion;
  }

  confirmar(): void {
    if (this.opcionSeleccionada) {
      this.seleccionar.emit(this.opcionSeleccionada);
      this.cerrar();
    }
  }

  cerrar(): void {
    this.opcionSeleccionada = '';
    this.cancelar.emit();
  }
}