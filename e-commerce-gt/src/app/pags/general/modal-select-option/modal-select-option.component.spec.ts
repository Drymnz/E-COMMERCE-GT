import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalSelectOptionComponent } from './modal-select-option.component';

describe('ModalSelectOptionComponent', () => {
  let component: ModalSelectOptionComponent;
  let fixture: ComponentFixture<ModalSelectOptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalSelectOptionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalSelectOptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
