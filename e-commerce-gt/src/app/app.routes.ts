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
import { PanelModeracionComponent } from './pags/moderator/panel-moderacion/panel-moderacion.component';
import { UsuariosListComponent } from './pags/moderator/usuarios-list/usuarios-list.component';
import { ListSanctionsComponent } from './pags/moderator/list-sanctions/list-sanctions.component';
import { OrderTrackingComponent } from './pags/customer/order-tracking/order-tracking.component';
import { OrderManagementComponent } from './pags/logistics/order-management/order-management.component';
import { ReportComponent } from './pags/admin/report/report.component';
import { NotificacionesComponent } from './pags/admin/notificacion/notificacion.component';
import { HistorialEmpleadosComponent } from './pags/admin/historial-empleados/historial-empleados.component';
import { GestionTarjetasComponent } from './pags/customer/manage-shopping-cart/gestion-tarjetas/gestion-tarjetas.component';
import { VentasComponent } from './pags/admin/ventas/ventas.component';

export const routes: Routes = [
  // RUTAS PROTEGIDAS
  {
    path: 'ventas',
    canActivate: [roleGuard(['4'])],
    component: VentasComponent
  },
  {
    path: 'admin',
    canActivate: [roleGuard(['4'])],
    component: AdminComponent
  },
  {
    path: 'report',
    canActivate: [roleGuard(['4'])],
    component: ReportComponent
  },
  {
    path: 'notificacion',
    canActivate: [roleGuard(['4'])],
    component: NotificacionesComponent
  },
  {
    path: 'historial-empleados',
    canActivate: [roleGuard(['4'])],
    component: HistorialEmpleadosComponent
  },
  // Cliente 
  {
    path: 'gestion-tarjetas',
    canActivate: [roleGuard(['1'])],
    component: GestionTarjetasComponent
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
  {
    path: 'order-tracking',
    canActivate: [roleGuard(['1'])],
    component: OrderTrackingComponent
  },
  {
    path: 'panel-moderator',
    canActivate: [roleGuard(['2'])],
    component: PanelModeracionComponent
  },
  //Moderador 
  {
    path: 'lits-usuario',
    canActivate: [roleGuard(['2'])],
    component: UsuariosListComponent
  },
  {
    path: 'lits-sanctions',
    canActivate: [roleGuard(['2','4'])],
    component: ListSanctionsComponent
  },
  {
    path: 'order-management',
    canActivate: [roleGuard(['3'])],
    component: OrderManagementComponent
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