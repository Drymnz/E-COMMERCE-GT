import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorialEmpleadosComponent } from './historial-empleados.component';

describe('HistorialEmpleadosComponent', () => {
  let component: HistorialEmpleadosComponent;
  let fixture: ComponentFixture<HistorialEmpleadosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialEmpleadosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistorialEmpleadosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
