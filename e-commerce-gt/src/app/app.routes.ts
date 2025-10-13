import { Routes } from '@angular/router';
import { HomeComponent } from './pags/general/home/home.component';
import { AdminComponent } from './pags/admin/admin.component';
import { LoginComponent } from './pags/general/login/login.component';
import { RegistroComponent } from './pags/general/registro/registro.component';
import { Error404Component } from './pags/general/error/error-404/error-404.component';
import { ManageProductsSaleComponent } from './pags/customer/manage-products-sale/manage-products-sale.component';
import { RegisterArticleComponent } from './pags/customer/register-article/register-article.component';
import { roleGuard } from './guards/auth.guard';

export const routes: Routes = [
  // RUTAS PROTEGIDAS
  {
    path: 'admin',
    canActivate: [roleGuard(['4'])],
    component: AdminComponent
  },
  {
    path: 'manage-products-sale',
    canActivate: [roleGuard(['1'])],
    component: ManageProductsSaleComponent
  },
  {
    path: 'register-article',
    canActivate: [roleGuard(['1'])],
    component: RegisterArticleComponent
  },
  {
    path: 'register-article/:id',
    canActivate: [roleGuard(['1'])],
    component: RegisterArticleComponent
  },
  // RUTAS PÚBLICAS 
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

  //  RUTA ERROR 
  {
    path: '404',
    component: Error404Component
  },
  {
    path: '**',
    component: Error404Component
  }
];