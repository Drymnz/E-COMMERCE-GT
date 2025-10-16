import { Routes } from '@angular/router';
import { HomeComponent } from './pags/general/home/home.component';
import { AdminComponent } from './pags/admin/admin.component';
import { LoginComponent } from './pags/general/login/login.component';
import { RegistroComponent } from './pags/general/registro/registro.component';
import { Error404Component } from './pags/general/error/error-404/error-404.component';
import { ManageProductsSaleComponent } from './pags/customer/manage-products-sale/manage-products-sale.component';
import { roleGuard } from './guards/auth.guard';
import { RegisterArticleComponent } from './pags/customer/manage-products-sale/register-article/register-article.component';
import { SeeProductComponent } from './pags/general/see-product/see-product.component';
import { ManageShoppingCartComponent } from './pags/customer/manage-shopping-cart/manage-shopping-cart.component';
import { PagoComponent } from './pags/general/pago/pago.component';

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
  {
    path: 'manage-shopping-cart',
    canActivate: [roleGuard(['1'])],
    component: ManageShoppingCartComponent
  },
  {
    path: 'payment-manager',
    canActivate: [roleGuard(['1'])],
    component: PagoComponent
  },
  // RUTAS PÚBLICAS 
  {
    path: 'see-product/:id',
    component: SeeProductComponent
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