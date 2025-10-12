import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageRatingsCommentsComponent } from './manage-ratings-comments.component';

describe('ManageRatingsCommentsComponent', () => {
  let component: ManageRatingsCommentsComponent;
  let fixture: ComponentFixture<ManageRatingsCommentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageRatingsCommentsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageRatingsCommentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
