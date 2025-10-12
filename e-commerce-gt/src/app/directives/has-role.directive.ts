import { Directive, Input, TemplateRef, ViewContainerRef, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';

@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class HasRoleDirective implements OnInit {
  private roles: string[] = [];

  // recibe uno o varios roles desde la vista
  @Input() set appHasRole(roles: string | string[]) {
    this.roles = Array.isArray(roles) ? roles : [roles];
    this.updateView();
  }

  constructor(
    private templateRef: TemplateRef<any>,     // referencia al contenido que se mostrará
    private viewContainer: ViewContainerRef,   // contenedor donde se renderiza la vista
    private authService: AuthService           // servicio para verificar roles del usuario
  ) {}

  ngOnInit(): void {
    this.updateView();
  }

  private updateView(): void {
    this.viewContainer.clear(); // limpia el contenido actual
    
    // si el usuario tiene el rol, muestra el contenido
    if (this.authService.hasAnyRole(this.roles)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
