import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanelModeracionComponent } from './panel-moderacion.component';

describe('PanelModeracionComponent', () => {
  let component: PanelModeracionComponent;
  let fixture: ComponentFixture<PanelModeracionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanelModeracionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanelModeracionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
