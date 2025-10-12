import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductCardManageComponent } from './product-card-manage.component';

describe('ProductCardManageComponent', () => {
  let component: ProductCardManageComponent;
  let fixture: ComponentFixture<ProductCardManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCardManageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductCardManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
