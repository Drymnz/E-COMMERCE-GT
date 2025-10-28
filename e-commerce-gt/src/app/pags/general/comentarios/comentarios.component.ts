import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Comentario } from '../../../entities/Comentario';
import { UserService } from '../../../service/api/user-service.service';

@Component({
  selector: 'app-comentarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './comentarios.component.html',
  styleUrl: './comentarios.component.scss'
})
export class ComentariosComponent implements OnInit {
  @Input() comentarios: Comentario[] = [];
  @Input() usuarioAutenticado: boolean = false;
  @Input() idUsuarioActual: number = 0;
  @Output() comentarioAgregado = new EventEmitter<{ descripcion: string, puntuacion: number }>();

  nuevaDescripcion = '';
  nuevaPuntuacion = 0;
  puntuacionSeleccionada = 0;
  errorSesion = false;
  errorDescripcion = false;
  errorPuntuacion = false;

  private nombresUsuarios = new Map<number, string>();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    [...new Set(this.comentarios.map(c => c.id_usuario))].forEach(id => this.obtenerNombreUsuario(id));
  }

  seleccionarPuntuacion(puntuacion: number): void {
    this.puntuacionSeleccionada = this.nuevaPuntuacion = puntuacion;
    this.errorPuntuacion = false;
  }

  publicarComentario(): void {
    this.errorSesion = this.errorDescripcion = this.errorPuntuacion = false;

    if (!this.usuarioAutenticado || this.idUsuarioActual === 0) {
      this.errorSesion = true;
      return;
    }
    if (this.nuevaDescripcion.trim() === '') {
      this.errorDescripcion = true;
      return;
    }
    if (this.nuevaPuntuacion === 0) {
      this.errorPuntuacion = true;
      return;
    }

    this.comentarioAgregado.emit({ descripcion: this.nuevaDescripcion, puntuacion: this.nuevaPuntuacion });
    this.nuevaDescripcion = '';
    this.nuevaPuntuacion = this.puntuacionSeleccionada = 0;
  }

  obtenerNombreUsuario(idUsuario: number): string {
    if (this.nombresUsuarios.has(idUsuario)) return this.nombresUsuarios.get(idUsuario)!;

    this.userService.buscarUsuario(idUsuario.toString()).subscribe({
      next: (usuario) => this.nombresUsuarios.set(idUsuario, usuario.nombreCompleto),
      error: () => this.nombresUsuarios.set(idUsuario, 'Usuario Anónimo')
    });

    return 'Cargando...';
  }

  esComentarioPropio(comentario: Comentario): boolean {
    return this.usuarioAutenticado && comentario.id_usuario === this.idUsuarioActual;
  }
}