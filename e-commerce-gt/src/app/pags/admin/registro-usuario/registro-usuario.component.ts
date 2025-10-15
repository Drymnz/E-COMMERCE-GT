import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../service/api/user-service.service';
import { ListConstantService } from '../../../service/api/list-constant.service';

@Component({
  selector: 'app-registro-usuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registro-usuario.component.html',
  styleUrl: './registro-usuario.component.scss'
})
export class RegistroUsuarioComponent implements OnInit {
  registroForm: FormGroup;
  cargando = false;
  mensajeExito = '';
  mensajeError = '';

  roles: string[] = [];
  estadosUsuario: string[] = [];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private constantService: ListConstantService
  ) {
    this.registroForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
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

  registrarNuevoUsuario(): void {
    if (this.registroForm.invalid) {
      Object.keys(this.registroForm.controls).forEach(key => {
        this.registroForm.get(key)?.markAsTouched();
      });
      return;
    }

    const formValue = this.registroForm.value;
    
    this.cargando = true;
    this.mensajeError = '';
    this.mensajeExito = '';
    
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
        this.mensajeError = 'Error al registrar usuario. Intente nuevamente.';
        console.error('Error al registrar usuario:', error);
      }
    });
  }
}