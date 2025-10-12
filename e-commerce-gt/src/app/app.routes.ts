import { Routes } from '@angular/router';
import { HomeComponent } from './pags/general/home/home.component';
import { AdminComponent } from './pags/admin/admin.component';
import { LoginComponent } from './pags/general/login/login.component';
import { RegistroComponent } from './pags/general/registro/registro.component';
import { authGuard } from './guards/auth.guard';
import { Error404Component } from './pags/general/error/error-404/error-404.component';
import { ManageProductsSaleComponent } from './pags/customer/manage-products-sale/manage-products-sale.component';
import { RegisterArticleComponent } from './pags/customer/register-article/register-article.component';

export const routes: Routes = [
  {
    path: 'admin',
    canActivate: [authGuard],
    component: AdminComponent
  },
  {
    path: 'manage-products-sale',
    canActivate: [authGuard],
    component: ManageProductsSaleComponent  
  },
  {
    path: 'register-article',
    canActivate: [authGuard],
    component: RegisterArticleComponent  
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
    path: '',
    component: HomeComponent  
  },
  {
    path: '**',
    component: Error404Component
  }
];