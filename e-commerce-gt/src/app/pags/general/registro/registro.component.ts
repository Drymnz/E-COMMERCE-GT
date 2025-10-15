import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../../service/api/user-service.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.scss'
})
export class RegistroComponent {
  registroForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;
  mostrarPassword: boolean = false;
  mostrarConfirmPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    // Inicialización del formulario
    this.registroForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      apellido: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordsIguales 
    });
  }

  // Validador de si la contraseñas coinciden
  passwordsIguales(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    // Si las contraseñas no coinciden
    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ noCoinciden: true });
      return { noCoinciden: true };
    }

    // Si coinciden y limpia el error
    if (confirmPassword.hasError('noCoinciden')) {
      confirmPassword.setErrors(null);
    }

    return null;
  }

  // Mostrar contraseña
  toggleMostrarPassword(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }

  // Mostrar contraseña
  toggleMostrarConfirmPassword(): void {
    this.mostrarConfirmPassword = !this.mostrarConfirmPassword;
  }

  // Enviar la informacion
  onRegistro(): void {
    // Si el formulario es inválido
    if (this.registroForm.invalid) {
      Object.keys(this.registroForm.controls).forEach(key => {
        this.registroForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.registroForm.value;

    // Servicio de crear
    this.userService.crearUsuario(
      formValue.nombre,
      formValue.apellido,
      formValue.email,
      formValue.password
    ).subscribe({
      next: (usuario) => {
        // Registro exitoso
        this.successMessage = 'Registro exitoso. Redirigiendo...';
        this.loading = false;

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error en registro:', error);
        
        // Manejo específico de diferentes tipos de errores
        if (error.status === 409) {
          this.errorMessage = 'El correo electrónico ya está registrado.';
        } else if (error.status === 400) {
          this.errorMessage = 'Datos inválidos. Verifique la información ingresada.';
        } else {
          this.errorMessage = 'Error al registrar usuario. Intente nuevamente.';
        }
        
        this.loading = false;
      }
    });
  }
}