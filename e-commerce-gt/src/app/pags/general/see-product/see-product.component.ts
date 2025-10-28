import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ArticleService } from '../../../service/api/article.service';
import { CommentService } from '../../../service/api/comment.service';
import { Articulo } from '../../../entities/Article';
import { ArticleViewComponent } from '../article-view/article-view.component';
import { Comentario } from '../../../entities/Comentario';
import { ComentariosComponent } from '../comentarios/comentarios.component';
import { AuthService } from '../../../service/local/auth.service';

@Component({
  selector: 'app-see-product',
  standalone: true,
  imports: [CommonModule, ArticleViewComponent, ComentariosComponent],
  templateUrl: './see-product.component.html',
  styleUrl: './see-product.component.scss'
})
export class SeeProductComponent implements OnInit {
  articulo: Articulo | null = null;
  cargando = true;
  error: string | null = null;
  puntuacionPromedio = 0;
  totalResenas = 0;
  comentarios: Comentario[] = [];
  cargandoComentarios = false;
  usuarioAutenticado = false;
  idUsuarioActual = 0;
  mensajeExito: string | null = null;
  mensajeError: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private articleService: ArticleService,
    private commentService: CommentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.usuarioAutenticado = this.authService.isAuthenticated();
    if (this.usuarioAutenticado) {
      const usuario = this.authService.currentUserValue;
      if (usuario?.id_usuario) this.idUsuarioActual = usuario.id_usuario;
    }
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) this.cargarArticulo(Number(id));
    });
  }

  cargarArticulo(id: number): void {
    this.cargando = true;
    this.error = null;

    this.articleService.getArticleById(id).subscribe({
      next: (articulo) => {
        if (articulo) {
          this.articulo = articulo;
          this.cargarComentarios(id);
        } else {
          this.error = 'Artículo no encontrado';
          this.cargando = false;
        }
      },
      error: () => {
        this.error = 'Error al cargar el artículo';
        this.cargando = false;
      }
    });
  }

  cargarComentarios(idArticulo: number): void {
    this.cargandoComentarios = true;

    this.commentService.getComentariosByArticulo(idArticulo).subscribe({
      next: (comentarios) => {
        this.comentarios = comentarios;
        this.calcularPuntuacion();
        this.cargando = this.cargandoComentarios = false;
      },
      error: () => {
        this.comentarios = [];
        this.cargando = this.cargandoComentarios = false;
      }
    });
  }

  calcularPuntuacion(): void {
    if (this.comentarios.length > 0) {
      this.puntuacionPromedio = this.commentService.calcularPuntuacionPromedio(this.comentarios);
      this.totalResenas = this.comentarios.length;
    } else {
      this.puntuacionPromedio = this.totalResenas = 0;
    }
  }

  agregarComentario(datos: { descripcion: string, puntuacion: number }): void {
    if (!this.usuarioAutenticado) {
      this.mostrarMensajeError('Debes iniciar sesión para comentar');
      setTimeout(() => this.router.navigate(['/login']), 2000);
      return;
    }

    if (!this.articulo) return;

    this.commentService.crearComentario(
      new Comentario(0, datos.descripcion, datos.puntuacion, this.idUsuarioActual, this.articulo.id_articulo)
    ).subscribe({
      next: (comentarioCreado) => {
        this.comentarios.unshift(comentarioCreado);
        this.calcularPuntuacion();
        this.mostrarMensajeExito('¡Comentario publicado exitosamente!');
      },
      error: (error) => {
        if (error.status === 401) {
          this.mostrarMensajeError('Tu sesión ha expirado');
          this.authService.logout();
          setTimeout(() => this.router.navigate(['/login']), 2000);
        } else {
          this.mostrarMensajeError('Error al publicar el comentario');
        }
      }
    });
  }

  private mostrarMensajeExito(mensaje: string): void {
    this.mensajeExito = mensaje;
    setTimeout(() => this.mensajeExito = null, 3000);
  }

  private mostrarMensajeError(mensaje: string): void {
    this.mensajeError = mensaje;
    setTimeout(() => this.mensajeError = null, 5000);
  }

  cerrarMensaje(tipo: 'exito' | 'error'): void {
    if (tipo === 'exito') this.mensajeExito = null;
    else this.mensajeError = null;
  }
}