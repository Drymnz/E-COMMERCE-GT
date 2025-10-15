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

  nuevaDescripcion: string = '';
  nuevaPuntuacion: number = 0;
  puntuacionSeleccionada: number = 0;
  errorSesion: boolean = false;
  errorDescripcion: boolean = false;
  errorPuntuacion: boolean = false;

  private nombresUsuarios: Map<number, string> = new Map();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.cargarNombresUsuarios();
  }

  private cargarNombresUsuarios(): void {
    const idsUnicos = [...new Set(this.comentarios.map(c => c.id_usuario))];
    idsUnicos.forEach(idUsuario => this.obtenerNombreUsuario(idUsuario));
  }

  seleccionarPuntuacion(puntuacion: number): void {
    this.puntuacionSeleccionada = puntuacion;
    this.nuevaPuntuacion = puntuacion;
    this.errorPuntuacion = false;
  }

  publicarComentario(): void {
    this.limpiarErrores();

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

    this.comentarioAgregado.emit({
      descripcion: this.nuevaDescripcion,
      puntuacion: this.nuevaPuntuacion
    });

    this.limpiarFormulario();
  }

  private limpiarErrores(): void {
    this.errorSesion = false;
    this.errorDescripcion = false;
    this.errorPuntuacion = false;
  }

  private limpiarFormulario(): void {
    this.nuevaDescripcion = '';
    this.nuevaPuntuacion = 0;
    this.puntuacionSeleccionada = 0;
  }

  obtenerNombreUsuario(idUsuario: number): string {
    if (this.nombresUsuarios.has(idUsuario)) {
      return this.nombresUsuarios.get(idUsuario)!;
    }

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