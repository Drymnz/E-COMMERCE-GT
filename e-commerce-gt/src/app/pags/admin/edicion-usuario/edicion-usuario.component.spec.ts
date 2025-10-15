import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EdicionUsuarioComponent } from './edicion-usuario.component';

describe('EdicionUsuarioComponent', () => {
  let component: EdicionUsuarioComponent;
  let fixture: ComponentFixture<EdicionUsuarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EdicionUsuarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EdicionUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
