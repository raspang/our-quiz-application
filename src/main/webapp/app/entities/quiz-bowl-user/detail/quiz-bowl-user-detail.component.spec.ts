import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuizBowlUserDetailComponent } from './quiz-bowl-user-detail.component';

describe('QuizBowlUser Management Detail Component', () => {
  let comp: QuizBowlUserDetailComponent;
  let fixture: ComponentFixture<QuizBowlUserDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizBowlUserDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: QuizBowlUserDetailComponent,
              resolve: { quizBowlUser: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(QuizBowlUserDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuizBowlUserDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load quizBowlUser on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', QuizBowlUserDetailComponent);

      // THEN
      expect(instance.quizBowlUser()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
