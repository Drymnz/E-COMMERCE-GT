import { Component, OnInit } from '@angular/core';
import { Usuario } from '../../../entities/Usuario';
import { UserService } from '../../../service/api/user-service.service';
import { ListConstantService } from '../../../service/api/list-constant.service';

@Component({
  selector: 'app-historial-empleados',
  imports: [],
  templateUrl: './historial-empleados.component.html',
  styleUrl: './historial-empleados.component.scss'
})
export class HistorialEmpleadosComponent implements OnInit {
  empleados: Usuario[] = [];
  estadosUsuario: string[] = [];
  roles: string[] = [];
  cargando = true;
  error = false;

  constructor(
    private userService: UserService,
    private listConstantService: ListConstantService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = false;

    this.listConstantService.estadosUsuario$.subscribe(
      estados => this.estadosUsuario = estados
    );

    this.listConstantService.roles$.subscribe(
      roles => this.roles = roles
    );

    this.userService.obtenerEmpleados().subscribe({
      next: (data) => {
        this.empleados = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar empleados:', err);
        this.error = true;
        this.cargando = false;
      }
    });
  }

  getNombreEstado(idEstado: string): string {
    const index = parseInt(idEstado) - 1;
    return this.estadosUsuario[index] || 'Desconocido';
  }

  getNombreRol(idRol: string): string {
    const index = parseInt(idRol) - 1;
    return this.roles[index] || 'Desconocido';
  }

  getClaseEstado(idEstado: string): string {
    return idEstado === '1' ? 'badge bg-success' : 'badge bg-danger';
  }

  getClaseRol(idRol: string): string {
    switch(idRol) {
      case '2': return 'badge bg-primary';
      case '3': return 'badge bg-warning';
      default: return 'badge bg-secondary';
    }
  }
}