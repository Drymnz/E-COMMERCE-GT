import { Component } from '@angular/core';
import { ListConstantService } from '../../service/list-constant.service';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html'
})
export class HomeComponent {
  categorias: string[] = [];

  constructor(
    private constantService: ListConstantService
  ) {

  }

  ngOnInit(): void {
    // obtener listas de roles y estados
    this.constantService.tiposCategorias$.subscribe(roles => {
      this.categorias = roles;
    });

  }
}