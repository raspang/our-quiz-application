import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../quiz-bowl-user.test-samples';

import { QuizBowlUserFormService } from './quiz-bowl-user-form.service';

describe('QuizBowlUser Form Service', () => {
  let service: QuizBowlUserFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuizBowlUserFormService);
  });

  describe('Service methods', () => {
    describe('createQuizBowlUserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuizBowlUserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            organization: expect.any(Object),
            score: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IQuizBowlUser should create a new form with FormGroup', () => {
        const formGroup = service.createQuizBowlUserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            organization: expect.any(Object),
            score: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuizBowlUser', () => {
      it('should return NewQuizBowlUser for default QuizBowlUser initial value', () => {
        const formGroup = service.createQuizBowlUserFormGroup(sampleWithNewData);

        const quizBowlUser = service.getQuizBowlUser(formGroup) as any;

        expect(quizBowlUser).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuizBowlUser for empty QuizBowlUser initial value', () => {
        const formGroup = service.createQuizBowlUserFormGroup();

        const quizBowlUser = service.getQuizBowlUser(formGroup) as any;

        expect(quizBowlUser).toMatchObject({});
      });

      it('should return IQuizBowlUser', () => {
        const formGroup = service.createQuizBowlUserFormGroup(sampleWithRequiredData);

        const quizBowlUser = service.getQuizBowlUser(formGroup) as any;

        expect(quizBowlUser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuizBowlUser should not enable id FormControl', () => {
        const formGroup = service.createQuizBowlUserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuizBowlUser should disable id FormControl', () => {
        const formGroup = service.createQuizBowlUserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
