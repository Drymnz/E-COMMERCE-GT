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

  passwordsIguales(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ noCoinciden: true });
      return { noCoinciden: true };
    }

    if (confirmPassword.hasError('noCoinciden')) {
      confirmPassword.setErrors(null);
    }

    return null;
  }

  toggleMostrarPassword(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }

  toggleMostrarConfirmPassword(): void {
    this.mostrarConfirmPassword = !this.mostrarConfirmPassword;
  }

  onRegistro(): void {
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

    this.userService.crearUsuario(
      formValue.nombre,
      formValue.apellido,
      formValue.email,
      formValue.password
    ).subscribe({
      next: (usuario) => {
        console.log('Registro exitoso:', usuario);
        this.successMessage = 'Registro exitoso. Redirigiendo...';
        this.loading = false;

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error en registro:', error);
        
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