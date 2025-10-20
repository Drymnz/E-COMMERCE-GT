import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Usuario } from '../../../entities/Usuario';
import { UserService } from '../../../service/api/user-service.service';
import { SancionService } from '../../../service/api/sancion.service';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { PaginatedResponse } from '../../../entities/PaginatedResponse';

@Component({
  selector: 'app-usuarios-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios-list.component.html',
  styleUrls: ['./usuarios-list.component.scss']
})
export class UsuariosListComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuariosSancionados: Usuario[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalUsuarios: number = 0;
  totalPages: number = 0;
  isLoading: boolean = false;
  isLoadingSancionados: boolean = false;

  //  sanción
  mostrarModalSancion: boolean = false;
  usuarioSeleccionado: Usuario | null = null;
  motivoSancion: string = '';
  nuevoEstadoUsuario: string = '3';
  
  //  cambio de estado
  mostrarModalCambioEstado: boolean = false;
  usuarioParaCambiarEstado: Usuario | null = null;
  nuevoEstadoSinSancion: string = '2'; 
  
  // Mensajes 
  mensajeExito: string = '';
  mensajeError: string = '';
  mostrarMensajeExito: boolean = false;
  mostrarMensajeError: boolean = false;

  // Listas de constantes
  roles: string[] = [];
  estadosUsuario: string[] = [];

  constructor(
    private userService: UserService,
    private sancionService: SancionService,
    private listConstantService: ListConstantService
  ) {}

  ngOnInit(): void {
    this.cargarConstantes();
    this.cargarUsuarios();
    this.cargarUsuariosSancionados();
  }

  cargarConstantes(): void {
    this.listConstantService.roles$.subscribe(roles => {
      this.roles = roles;
    });

    this.listConstantService.estadosUsuario$.subscribe(estados => {
      this.estadosUsuario = estados;
    });
  }

  cargarUsuarios(): void {
    this.isLoading = true;
    this.ocultarMensajes();
    this.userService.obtenerUsuariosPaginados(this.currentPage, this.pageSize)
      .subscribe({
        next: (response: PaginatedResponse) => {
          console.log('Todos los usuarios recibidos:', response.usuarios);
          // Filtrar  "Activo"
          this.usuarios = response.usuarios.filter(usuario => {
            console.log(`Usuario ${usuario.nombre}: id_estado = ${usuario.id_estado}, tipo: ${typeof usuario.id_estado}`);
            return usuario.id_estado === '2' || usuario.id_estado === String(2);
          });
          console.log('Usuarios activos filtrados:', this.usuarios);
          this.totalUsuarios = response.totalUsuarios;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error al cargar usuarios:', error);
          this.mostrarMensajeErrorTemporal('Error al cargar la lista de usuarios');
          this.isLoading = false;
        }
      });
  }

  cargarUsuariosSancionados(): void {
    this.isLoadingSancionados = true;
    this.userService.obtenerUsuariosPaginados(1, 100) 
      .subscribe({
        next: (response: PaginatedResponse) => {
          console.log('Todos los usuarios para sancionados:', response.usuarios);
          // Filtrar diferentes a "Activo" 
          this.usuariosSancionados = response.usuarios.filter(usuario => {
            console.log(`Usuario sancionado ${usuario.nombre}: id_estado = ${usuario.id_estado}`);
            return usuario.id_estado !== '2' && usuario.id_estado !== String(2);
          });
          console.log('Usuarios sancionados filtrados:', this.usuariosSancionados);
          this.isLoadingSancionados = false;
        },
        error: (error) => {
          console.error('Error al cargar usuarios sancionados:', error);
          this.mostrarMensajeErrorTemporal('Error al cargar usuarios sancionados');
          this.isLoadingSancionados = false;
        }
      });
  }

  cambiarPagina(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.cargarUsuarios();
    }
  }

  paginaAnterior(): void {
    this.cambiarPagina(this.currentPage - 1);
  }

  paginaSiguiente(): void {
    this.cambiarPagina(this.currentPage + 1);
  }

  obtenerPaginas(): number[] {
    const paginas: number[] = [];
    const maxPaginas = 5;
    let inicio = Math.max(1, this.currentPage - Math.floor(maxPaginas / 2));
    let fin = Math.min(this.totalPages, inicio + maxPaginas - 1);

    if (fin - inicio < maxPaginas - 1) {
      inicio = Math.max(1, fin - maxPaginas + 1);
    }

    for (let i = inicio; i <= fin; i++) {
      paginas.push(i);
    }

    return paginas;
  }

  abrirModalSancion(usuario: Usuario): void {
    this.usuarioSeleccionado = usuario;
    this.motivoSancion = '';
    this.nuevoEstadoUsuario = '3';
    this.mostrarModalSancion = true;
  }

  cerrarModalSancion(): void {
    this.mostrarModalSancion = false;
    this.usuarioSeleccionado = null;
    this.motivoSancion = '';
    this.nuevoEstadoUsuario = '3';
  }

  aplicarSancion(): void {
    if (!this.usuarioSeleccionado || !this.motivoSancion.trim()) {
      this.mostrarMensajeErrorTemporal('Por favor ingrese un motivo para la sanción');
      return;
    }

    // Primero aplicar la sanción
    this.sancionService.crearSancion(this.motivoSancion, this.usuarioSeleccionado.id_usuario)
      .subscribe({
        next: () => {
          // Luego actualizar el estado del usuario
          if (this.usuarioSeleccionado) {
            // Actualizar directamente la propiedad del usuario
            this.usuarioSeleccionado.id_estado = this.nuevoEstadoUsuario;

            this.userService.modificarUsuario(this.usuarioSeleccionado).subscribe({
              next: () => {
                this.mostrarMensajeExitoTemporal('Sanción aplicada y estado actualizado correctamente');
                this.cerrarModalSancion();
                this.cargarUsuarios();
                this.cargarUsuariosSancionados(); 
              },
              error: (error) => {
                console.error('Error al actualizar estado del usuario:', error);
                this.mostrarMensajeErrorTemporal('Sanción aplicada pero hubo un error al actualizar el estado');
                this.cerrarModalSancion();
                this.cargarUsuarios();
                this.cargarUsuariosSancionados();
              }
            });
          }
        },
        error: (error) => {
          console.error('Error al aplicar sanción:', error);
          this.mostrarMensajeErrorTemporal('Error al aplicar la sanción');
        }
      });
  }

  // Modal para cambiar estado de usuario sancionado
  abrirModalCambioEstado(usuario: Usuario): void {
    this.usuarioParaCambiarEstado = usuario;
    this.nuevoEstadoSinSancion = '2'; 
    this.mostrarModalCambioEstado = true;
  }

  cerrarModalCambioEstado(): void {
    this.mostrarModalCambioEstado = false;
    this.usuarioParaCambiarEstado = null;
    this.nuevoEstadoSinSancion = '2';
  }

  cambiarEstadoUsuario(): void {
    if (!this.usuarioParaCambiarEstado) {
      this.mostrarMensajeErrorTemporal('No se ha seleccionado ningún usuario');
      return;
    }

    // Actualizar el estado del usuario
    this.usuarioParaCambiarEstado.id_estado = this.nuevoEstadoSinSancion;

    this.userService.modificarUsuario(this.usuarioParaCambiarEstado).subscribe({
      next: () => {
        this.mostrarMensajeExitoTemporal('Estado del usuario actualizado correctamente');
        this.cerrarModalCambioEstado();
        this.cargarUsuarios();
        this.cargarUsuariosSancionados(); 
      },
      error: (error) => {
        console.error('Error al cambiar estado del usuario:', error);
        this.mostrarMensajeErrorTemporal('Error al cambiar el estado del usuario');
      }
    });
  }

  // Métodos para mostrar mensajes
  mostrarMensajeExitoTemporal(mensaje: string): void {
    this.mensajeExito = mensaje;
    this.mostrarMensajeExito = true;
    setTimeout(() => {
      this.mostrarMensajeExito = false;
    }, 4000);
  }

  mostrarMensajeErrorTemporal(mensaje: string): void {
    this.mensajeError = mensaje;
    this.mostrarMensajeError = true;
    setTimeout(() => {
      this.mostrarMensajeError = false;
    }, 4000);
  }

  ocultarMensajes(): void {
    this.mostrarMensajeExito = false;
    this.mostrarMensajeError = false;
  }

  obtenerNombreRol(idRol: string): string {
    const index = parseInt(idRol) - 1;
    return this.roles[index] || 'Desconocido';
  }

  obtenerNombreEstado(idEstado: string): string {
    const index = parseInt(idEstado) - 1;
    return this.estadosUsuario[index] || 'Desconocido';
  }

  obtenerClaseBadgeEstado(idEstado: string): string {
    const clases: { [key: string]: string } = {
      '1': 'bg-warning',   
      '2': 'bg-success',   
      '3': 'bg-danger'     
    };
    return clases[idEstado] || 'bg-secondary';
  }
}