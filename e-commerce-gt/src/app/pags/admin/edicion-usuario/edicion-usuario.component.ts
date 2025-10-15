import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../service/api/user-service.service';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { Usuario } from '../../../entities/Usuario';

@Component({
  selector: 'app-edicion-usuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edicion-usuario.component.html',
  styleUrl: './edicion-usuario.component.scss'
})
export class EdicionUsuarioComponent implements OnInit {
  busquedaForm: FormGroup;
  edicionForm: FormGroup;

  usuarioEncontrado: Usuario | null = null;
  cargando = false;
  errorBusqueda = '';
  mensajeExito = '';

  roles: string[] = [];
  estadosUsuario: string[] = [];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private constantService: ListConstantService
  ) {
    this.busquedaForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    this.edicionForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      id_rol: ['', Validators.required],
      id_estado: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.constantService.roles$.subscribe(roles => {
      this.roles = roles;
    });

    this.constantService.estadosUsuario$.subscribe(estados => {
      this.estadosUsuario = estados;
    });
  }

  buscarUsuario(): void {
    if (this.busquedaForm.invalid) {
      this.errorBusqueda = 'Por favor ingrese un correo electrónico válido';
      return;
    }

    this.cargando = true;
    this.errorBusqueda = '';
    this.mensajeExito = '';
    this.usuarioEncontrado = null;

    const email = this.busquedaForm.get('email')?.value;

    this.userService.buscarUsuario(email).subscribe({
      next: (usuario) => {
        this.usuarioEncontrado = usuario;
        this.cargarDatosEdicion(usuario);
        this.mensajeExito = `Usuario encontrado: ${usuario.nombreCompleto}`;
        this.cargando = false;
      },
      error: (error) => {
        this.cargando = false;
        if (error.status === 404) {
          this.errorBusqueda = 'Usuario no encontrado';
        } else {
          this.errorBusqueda = 'Error al buscar usuario. Intente nuevamente.';
        }
        console.error('Error al buscar usuario:', error);
      }
    });
  }

  private cargarDatosEdicion(usuario: Usuario): void {
    this.edicionForm.patchValue({
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      id_rol: usuario.id_rol,
      id_estado: usuario.id_estado
    });
  }

  actualizarUsuario(): void {
    if (this.edicionForm.invalid || !this.usuarioEncontrado) return;

    const formValue = this.edicionForm.value;
    const usuarioActualizado = Usuario.crearDesdeDatos(
      this.usuarioEncontrado.id_usuario,
      formValue.nombre,
      formValue.apellido,
      this.usuarioEncontrado.email,
      formValue.id_estado,
      formValue.id_rol
    );

    this.cargando = true;
    this.errorBusqueda = '';
    this.mensajeExito = '';

    this.userService.modificarUsuario(usuarioActualizado).subscribe({
      next: (usuario) => {
        this.usuarioEncontrado = usuario;
        this.cargando = false;
        this.mensajeExito = 'Usuario actualizado correctamente';
      },
      error: (error) => {
        this.cargando = false;
        this.errorBusqueda = 'Error al actualizar usuario. Intente nuevamente.';
        console.error('Error al actualizar usuario:', error);
      }
    });
  }

  limpiarBusqueda(): void {
    this.busquedaForm.reset();
    this.edicionForm.reset();
    this.usuarioEncontrado = null;
    this.errorBusqueda = '';
    this.mensajeExito = '';
  }
}