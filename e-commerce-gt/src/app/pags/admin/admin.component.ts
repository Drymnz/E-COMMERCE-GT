import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../service/user-service.service';
import { ListConstantService } from '../../service/list-constant.service';
import { Usuario } from '../../entities/Usuario';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
  registroForm: FormGroup;
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
    // Formulario para registrar usuario
    this.registroForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      id_rol: ['', Validators.required],
      id_estado: ['', Validators.required]
    });

    // Formulario para buscar usuario
    this.busquedaForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    // Formulario para editar usuario
    this.edicionForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      id_rol: ['', Validators.required],
      id_estado: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Obtener listas de roles y estados
    this.constantService.roles$.subscribe(roles => {
      this.roles = roles;
    });

    this.constantService.estadosUsuario$.subscribe(estados => {
      this.estadosUsuario = estados;
    });
  }

  // Buscar usuario por email
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

  // Cargar datos del usuario en el formulario de edición
  private cargarDatosEdicion(usuario: Usuario): void {
    this.edicionForm.patchValue({
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      id_rol: usuario.id_rol,
      id_estado: usuario.id_estado
    });
  }

  // Actualizar usuario existente
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

  // Limpiar formularios y mensajes
  limpiarBusqueda(): void {
    this.busquedaForm.reset();
    this.edicionForm.reset();
    this.usuarioEncontrado = null;
    this.errorBusqueda = '';
    this.mensajeExito = '';
  }

  // Registrar nuevo usuario
  registrarNuevoUsuario(): void {
    if (this.registroForm.invalid) {
      Object.keys(this.registroForm.controls).forEach(key => {
        this.registroForm.get(key)?.markAsTouched();
      });
      return;
    }

    const formValue = this.registroForm.value;
    
    this.cargando = true;
    this.errorBusqueda = '';
    this.mensajeExito = '';
    
    // Convertir strings a números antes de enviar
    this.userService.crearUsuario(
      formValue.nombre,
      formValue.apellido,
      formValue.email,
      formValue.password,
      Number(formValue.id_rol),
      Number(formValue.id_estado)
    ).subscribe({
      next: () => {
        this.cargando = false;
        this.mensajeExito = 'Usuario registrado correctamente';
        this.registroForm.reset();
      },
      error: (error) => {
        this.cargando = false;
        this.errorBusqueda = 'Error al registrar usuario. Intente nuevamente.';
        console.error('Error al registrar usuario:', error);
      }
    });
  }
}