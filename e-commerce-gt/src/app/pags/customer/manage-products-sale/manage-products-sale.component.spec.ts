import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageProductsSaleComponent } from './manage-products-sale.component';

describe('ManageProductsSaleComponent', () => {
  let component: ManageProductsSaleComponent;
  let fixture: ComponentFixture<ManageProductsSaleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageProductsSaleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageProductsSaleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
