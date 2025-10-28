import { Component } from '@angular/core';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../service/api/user-service.service';
import { AuthService } from '../../../service/local/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email = '';
  password = '';
  loading = false;
  errorMessage = '';
  returnUrl: string;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onLogin(): void {
    if (!this.email || !this.password) {
      this.errorMessage = 'Por favor complete todos los campos';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.userService.login(this.email, this.password).subscribe({
      next: (usuario) => {
        this.authService.login(usuario);
        this.router.navigate([this.returnUrl]);
      },
      error: (error) => {
        console.error('Error en login:', error);
        this.errorMessage = 'Credenciales incorrectas. Intente nuevamente.';
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }
}