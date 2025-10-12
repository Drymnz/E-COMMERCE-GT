import { Routes } from '@angular/router';
import { HomeComponent } from './pags/home/home.component';
import { AdminComponent } from './pags/admin/admin.component';
import { LoginComponent } from './pags/login/login.component';
import { RegistroComponent } from './pags/registro/registro.component';
import { roleGuard } from './guards/auth.guard';
import { Error404Component } from './pags/error/error-404/error-404.component';

export const routes: Routes = [
  {
    path: 'admin',
    canActivate: [roleGuard(['4'])],
    component: AdminComponent
  },
  {
    path: '',
    component: HomeComponent  
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'registro',
    component: RegistroComponent
  },
  {
    path: '**',
    component: Error404Component
  }
];