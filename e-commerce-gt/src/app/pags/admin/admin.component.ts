import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegistroUsuarioComponent } from "./registro-usuario/registro-usuario.component";
import { EdicionUsuarioComponent } from "./edicion-usuario/edicion-usuario.component";

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RegistroUsuarioComponent, EdicionUsuarioComponent],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {
  
}