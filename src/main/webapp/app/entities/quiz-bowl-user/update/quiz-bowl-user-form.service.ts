import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IQuizBowlUser, NewQuizBowlUser } from '../quiz-bowl-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuizBowlUser for edit and NewQuizBowlUserFormGroupInput for create.
 */
type QuizBowlUserFormGroupInput = IQuizBowlUser | PartialWithRequiredKeyOf<NewQuizBowlUser>;

type QuizBowlUserFormDefaults = Pick<NewQuizBowlUser, 'id'>;

type QuizBowlUserFormGroupContent = {
  id: FormControl<IQuizBowlUser['id'] | NewQuizBowlUser['id']>;
  score: FormControl<IQuizBowlUser['score']>;
  organization: FormControl<IQuizBowlUser['organization']>;
  user: FormControl<IQuizBowlUser['user']>;
};

export type QuizBowlUserFormGroup = FormGroup<QuizBowlUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class QuizBowlUserFormService {
  createQuizBowlUserFormGroup(quizBowlUser: QuizBowlUserFormGroupInput = { id: null }): QuizBowlUserFormGroup {
    const quizBowlUserRawValue = {
      ...this.getFormDefaults(),
      ...quizBowlUser,
      score: quizBowlUser.score ?? 0, // Initialize score to 0 if it's not provided
    };
    return new FormGroup<QuizBowlUserFormGroupContent>({
      id: new FormControl(
        { value: quizBowlUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      score: new FormControl(quizBowlUserRawValue.score),
      organization: new FormControl(quizBowlUserRawValue.organization),
      user: new FormControl(quizBowlUserRawValue.user),
    });
  }

  getQuizBowlUser(form: QuizBowlUserFormGroup): IQuizBowlUser | NewQuizBowlUser {
    return form.getRawValue() as IQuizBowlUser | NewQuizBowlUser;
  }

  resetForm(form: QuizBowlUserFormGroup, quizBowlUser: QuizBowlUserFormGroupInput): void {
    const quizBowlUserRawValue = { ...this.getFormDefaults(), ...quizBowlUser };
    form.reset(
      {
        ...quizBowlUserRawValue,
        id: { value: quizBowlUserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): QuizBowlUserFormDefaults {
    return {
      id: null,
    };
  }
}
